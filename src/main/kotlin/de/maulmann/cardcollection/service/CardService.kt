package de.maulmann.cardcollection.service
import de.maulmann.cardcollection.model.*
import de.maulmann.cardcollection.repository.*
import jakarta.persistence.criteria.JoinType
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
@Service
class CardService(
    private val cardRepository: CardRepository,
    private val cardBrandRepository: CardBrandRepository,
    private val cardThemeRepository: CardThemeRepository,
    private val sportRepository: SportRepository,
    private val variantRepository: VariantRepository,
    private val teamRepository: TeamRepository, // New injection
    private val seasonRepository: SeasonRepository, // Add SeasonRepository
    private val cardManufacturerRepository: CardManufacturerRepository
) {
    fun getCardsFiltered(
        manufacturerId: Long?,
        brandId: Long?,
        themeId: Long?,
        sportId: Long?,
        playerId: Long?,
        seasonId: Long?, // Changed from season: String?
        gameUsed: Boolean?, // New
        autograph: Boolean?,  // New
        variantId: Long?,
        rookieCard: Boolean?,
        printRunRangeKey: String?,
        teamId: Long?,
        isGradedNullable: Boolean?, // New parameter for grading status
        pageable: Pageable
    ): Page<Card> {
        val specifications = mutableListOf<Specification<Card>>()
        manufacturerId?.let {
            specifications.add(Specification { root, _, cb ->
                cb.equal(root.get<CardTheme>("theme").get<CardBrand>("brand").get<CardManufacturer>("manufacturer").get<Long>("id"), it)
            })
        }
        brandId?.let {
            specifications.add(Specification { root, _, cb ->
                cb.equal(root.get<CardTheme>("theme").get<CardBrand>("brand").get<Long>("id"), it)
            })
        }
        themeId?.let {
            specifications.add(Specification { root, _, cb ->
                cb.equal(root.get<CardTheme>("theme").get<Long>("id"), it)
            })
        }
        sportId?.let {
            specifications.add(Specification { root, _, cb ->
                cb.equal(root.get<Player>("player").get<Sport>("sport").get<Long>("id"), it)
            })
        }
        playerId?.let {
            specifications.add(Specification { root, _, cb ->
                cb.equal(root.get<Player>("player").get<Long>("id"), it)
            })
        }
        seasonId?.let { // Changed from season to seasonId
            specifications.add(Specification { root, _, cb ->
                cb.equal(root.get<Season>("season").get<Long>("id"), it) // Updated specification for Season
            })
        }
        gameUsed?.let {
            specifications.add(Specification { root, _, cb ->
                cb.equal(root.get<Boolean>("gameUsedMaterial"), it)
            })
        }
        autograph?.let {
            specifications.add(Specification { root, _, cb ->
                cb.equal(root.get<Boolean>("autograph"), it)
            })
        }
        variantId?.let {
            specifications.add(Specification { root, _, cb ->
                cb.equal(root.get<Variant>("variant").get<Long>("id"), it)
            })
        }
        rookieCard?.let {
            specifications.add(Specification { root, _, cb ->
                cb.equal(root.get<Boolean>("rookieCard"), it)
            })
        }
        teamId?.let {
            specifications.add(Specification { root, _, cb ->
                cb.equal(root.get<Team>("team").get<Long>("id"), it)
            })
        }
        // Handle isGradedNullable filter
        isGradedNullable?.let { isGraded ->
            specifications.add(Specification { root, _, cb ->
                if (isGraded) {
                    cb.isNotNull(root.get<Any>("grading")) // Check if 'grading' field is not null
                } else {
                    cb.isNull(root.get<Any>("grading")) // Check if 'grading' field is null
                }
            })
        }
        // Handle PrintRunRange
        val selectedPrintRunRange = PrintRunRange.fromKey(printRunRangeKey)
        selectedPrintRunRange?.let { range ->
            specifications.add(Specification { root, _, cb ->
                when (range) {
                    PrintRunRange.ONE -> cb.equal(root.get<Int>("printRun"), 1)
                    PrintRunRange.LE_10 -> cb.and(
                        cb.greaterThan(root.get("printRun"), 0),
                        cb.lessThanOrEqualTo(root.get("printRun"), 10)
                    )
                    PrintRunRange.LE_50 -> cb.and(
                        cb.greaterThan(root.get("printRun"), 0),
                        cb.lessThanOrEqualTo(root.get("printRun"), 50)
                    )
                    PrintRunRange.LE_100 -> cb.and(
                        cb.greaterThan(root.get("printRun"), 0),
                        cb.lessThanOrEqualTo(root.get("printRun"), 100)
                    )
                    PrintRunRange.LE_500 -> cb.and(
                        cb.greaterThan(root.get("printRun"), 0),
                        cb.lessThanOrEqualTo(root.get("printRun"), 500)
                    )
                    PrintRunRange.LE_1000 -> cb.and(
                        cb.greaterThan(root.get("printRun"), 0),
                        cb.lessThanOrEqualTo(root.get("printRun"), 1000)
                    )
                    PrintRunRange.ALL_WITH_PRINT_RUN -> cb.greaterThan(root.get("printRun"), 0)
                }
            })
        }
        // Combine all specifications
        var finalSpecification = specifications.reduceOrNull { acc, spec -> acc.and(spec) }
            ?: Specification { _, _, _ -> null } // If no filters, return all
        // Optimization to avoid N+1 queries by join fetching related entities
        val fetchSpecification = Specification<Card> { root, query, cb ->
            val resultType = query.resultType
            if (resultType != Long::class.javaObjectType && resultType != Long::class.javaPrimitiveType && resultType.simpleName != "Long") { // Only fetch if not a count query
                root.fetch<Card, Season>("season", JoinType.LEFT)
                val playerFetch = root.fetch<Card, Player>("player", JoinType.LEFT)
                playerFetch.fetch<Player, Sport>("sport", JoinType.LEFT)
                root.fetch<Card, Team>("team", JoinType.LEFT)
                root.fetch<Card, Variant>("variant", JoinType.LEFT)
                val themeFetch = root.fetch<Card, CardTheme>("theme", JoinType.LEFT)
                val brandFetch = themeFetch.fetch<CardTheme, CardBrand>("brand", JoinType.LEFT)
                brandFetch.fetch<CardBrand, CardManufacturer>("manufacturer", JoinType.LEFT)
                root.fetch<Card, Grading>("grading", JoinType.LEFT)
            }
            null
        }
        finalSpecification = finalSpecification.and(fetchSpecification)
        return cardRepository.findAll(finalSpecification, pageable)
    }
    // New service methods to fetch data for filter dropdowns
    @Cacheable("brands")
    fun getAllBrands(manufacturerId: Long? = null): List<CardBrand> {
        return if (manufacturerId != null) {
            cardBrandRepository.findAllByManufacturerIdOrderByNameAsc(manufacturerId)
        } else {
            cardBrandRepository.findAllByOrderByNameAsc()
        }
    }
    @Cacheable("themes")
    fun getAllThemes(manufacturerId: Long? = null, brandId: Long? = null): List<CardTheme> {
        return when {
            brandId != null -> cardThemeRepository.findAllByBrandIdOrderByNameAsc(brandId)
            manufacturerId != null -> cardThemeRepository.findAllByBrandManufacturerIdOrderByNameAsc(manufacturerId)
            else -> cardThemeRepository.findAllByOrderByNameAsc()
        }
    }
    @Cacheable("sports")
    fun getAllSports(): List<Sport> {
        return sportRepository.findAll()
    }
    @Cacheable("seasons")
    fun getAllSeasons(): List<Season> {
        return seasonRepository.findAllByOrderByNameAsc()
    }
    @Cacheable("variants")
    fun getAllVariants(): List<Variant> {
        return variantRepository.findAll()
    }
    @Cacheable("teams")
    fun getAllTeams(): List<Team> { //This might need to be adjusted if we want it to be dynamic based on current filters
        return teamRepository.findAll()
    }
    @Cacheable("manufacturers")
    fun getAllCardManufacturers(): List<CardManufacturer> {
        return cardManufacturerRepository.findAll()
    }
}
