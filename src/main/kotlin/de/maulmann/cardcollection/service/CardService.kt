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
    private val teamRepository: TeamRepository,
    private val seasonRepository: SeasonRepository,
    private val cardManufacturerRepository: CardManufacturerRepository
) {

    @Cacheable("filteredCards")
    fun getCardsFiltered(
        manufacturerId: Long?,
        brandId: Long?,
        themeId: Long?,
        sportId: Long?,
        playerId: Long?,
        seasonId: Long?,
        gameUsed: Boolean?,
        autograph: Boolean?,
        variantId: Long?,
        rookieCard: Boolean?,
        printRunRangeKey: String?,
        teamId: Long?,
        isGradedNullable: Boolean?,
        pageable: Pageable
    ): Page<Card> {
        val specifications = mutableListOf<Specification<Card>>()

        manufacturerId?.let {
            specifications.add(Specification { root, _, cb ->
                cb.equal(root.get<CardManufacturer>("manufacturer").get<Long>("id"), it)
            })
        }
        brandId?.let {
            specifications.add(Specification { root, _, cb ->
                cb.equal(root.get<CardBrand>("brand").get<Long>("id"), it)
            })
        }
        themeId?.let {
            specifications.add(Specification { root, _, cb ->
                cb.equal(root.get<CardTheme>("theme").get<Long>("id"), it)
            })
        }
        sportId?.let {
            specifications.add(Specification { root, _, cb ->
                cb.equal(root.join<Card, CardPlayer>("cardPlayers").get<Player>("player").get<Sport>("sport").get<Long>("id"), it)
            })
        }
        playerId?.let {
            specifications.add(Specification { root, _, cb ->
                cb.equal(root.join<Card, CardPlayer>("cardPlayers").get<Player>("player").get<Long>("id"), it)
            })
        }
        seasonId?.let {
            specifications.add(Specification { root, _, cb ->
                cb.equal(root.get<Season>("season").get<Long>("id"), it)
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
                cb.equal(root.join<Card, CardPlayer>("cardPlayers").get<Team>("team").get<Long>("id"), it)
            })
        }
        isGradedNullable?.let { isGraded ->
            specifications.add(Specification { root, _, cb ->
                if (isGraded) {
                    cb.isNotNull(root.get<Any>("grading"))
                } else {
                    cb.isNull(root.get<Any>("grading"))
                }
            })
        }

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

        var finalSpecification = specifications.reduceOrNull { acc, spec -> acc.and(spec) }
            ?: Specification { _, _, _ -> null }

        val distinctSpecification = Specification<Card> { _, query, cb ->
            query.distinct(true)
            null
        }
        finalSpecification = finalSpecification.and(distinctSpecification)

        val fetchSpecification = Specification<Card> { root, query, cb ->
            val resultType = query.resultType
            if (resultType != Long::class.java && resultType != java.lang.Long::class.java && resultType.simpleName != "Long") {
                root.fetch<Card, Season>("season", JoinType.LEFT)
                root.fetch<Card, Variant>("variant", JoinType.LEFT)
                root.fetch<Card, CardTheme>("theme", JoinType.LEFT)
                root.fetch<Card, CardBrand>("brand", JoinType.LEFT)
                root.fetch<Card, CardManufacturer>("manufacturer", JoinType.LEFT)
                root.fetch<Card, Grading>("grading", JoinType.LEFT)
            }
            null
        }

        finalSpecification = finalSpecification.and(fetchSpecification)

        return cardRepository.findAll(finalSpecification, pageable)
    }

    @Cacheable("brands")
    fun getAllBrands(manufacturerId: Long? = null): List<CardBrand> {
        return if (manufacturerId != null) {
            cardRepository.findDistinctBrandsByManufacturerId(manufacturerId)
        } else {
            cardBrandRepository.findAllByOrderByNameAsc()
        }
    }

    @Cacheable("themes")
    fun getAllThemes(manufacturerId: Long? = null, brandId: Long? = null): List<CardTheme> {
        return if (manufacturerId != null || brandId != null) {
            cardRepository.findDistinctThemesByManufacturerIdAndBrandId(manufacturerId, brandId)
        } else {
            cardThemeRepository.findAllByOrderByNameAsc()
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
    fun getAllVariants(
        manufacturerId: Long? = null,
        brandId: Long? = null,
        themeId: Long? = null
    ): List<Variant> {
        return if (manufacturerId != null || brandId != null || themeId != null) {
            cardRepository.findDistinctVariantsByFilter(manufacturerId, brandId, themeId)
        } else {
            variantRepository.findAll()
        }
    }

    @Cacheable("teams")
    fun getAllTeams(): List<Team> {
        return teamRepository.findAll()
    }

    @Cacheable("manufacturers")
    fun getAllCardManufacturers(): List<CardManufacturer> {
        return cardManufacturerRepository.findAll()
    }
}