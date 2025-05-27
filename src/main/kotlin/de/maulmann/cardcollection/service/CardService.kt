package de.maulmann.cardcollection.service

import de.maulmann.cardcollection.model.Card
import de.maulmann.cardcollection.model.CardBrand
import de.maulmann.cardcollection.model.CardTheme
import de.maulmann.cardcollection.model.Player
import de.maulmann.cardcollection.model.Sport
import de.maulmann.cardcollection.model.Team // Import Team
import de.maulmann.cardcollection.model.CardManufacturer
import de.maulmann.cardcollection.model.Variant
import de.maulmann.cardcollection.model.Season // Import Season model
import de.maulmann.cardcollection.repository.CardBrandRepository
import de.maulmann.cardcollection.repository.CardRepository
import de.maulmann.cardcollection.repository.CardThemeRepository
import de.maulmann.cardcollection.repository.SportRepository
import de.maulmann.cardcollection.repository.TeamRepository // Import TeamRepository
import de.maulmann.cardcollection.repository.VariantRepository
import de.maulmann.cardcollection.repository.SeasonRepository // Import SeasonRepository
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
    private val seasonRepository: SeasonRepository // Add SeasonRepository
) {

    fun getAllCards(): List<Card> = cardRepository.findAllWithDetails()
    fun getCardById(id: Long): Card? = cardRepository.findById(id).orElse(null) // For single card, findById is fine. Details can be fetched if needed by EntityGraph on Card or specific DTO projection.
    

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
        printRunRangeKey: String?, // New parameter
        teamId: Long?, // New parameter
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
                cb.equal(root.get<Player>("player").get<Team>("team").get<Long>("id"), it)
            })
        }

        // Handle PrintRunRange
        val selectedPrintRunRange = PrintRunRange.fromKey(printRunRangeKey)
        selectedPrintRunRange?.let { range ->
            specifications.add(Specification { root, _, cb ->
                when (range) {
                    PrintRunRange.ONE -> cb.equal(root.get<Int>("printRun"), 1)
                    PrintRunRange.LE_10 -> cb.and(
                        cb.greaterThan(root.get<Int>("printRun"), 0),
                        cb.lessThanOrEqualTo(root.get<Int>("printRun"), 10)
                    )
                    PrintRunRange.LE_50 -> cb.and(
                        cb.greaterThan(root.get<Int>("printRun"), 0),
                        cb.lessThanOrEqualTo(root.get<Int>("printRun"), 50)
                    )
                    PrintRunRange.LE_100 -> cb.and(
                        cb.greaterThan(root.get<Int>("printRun"), 0),
                        cb.lessThanOrEqualTo(root.get<Int>("printRun"), 100)
                    )
                    PrintRunRange.LE_500 -> cb.and(
                        cb.greaterThan(root.get<Int>("printRun"), 0),
                        cb.lessThanOrEqualTo(root.get<Int>("printRun"), 500)
                    )
                    PrintRunRange.LE_1000 -> cb.and(
                        cb.greaterThan(root.get<Int>("printRun"), 0),
                        cb.lessThanOrEqualTo(root.get<Int>("printRun"), 1000)
                    )
                    PrintRunRange.ALL_WITH_PRINT_RUN -> cb.greaterThan(root.get<Int>("printRun"), 0)
                }
            })
        }

        // Combine all specifications
        val finalSpecification = specifications.reduceOrNull { acc, spec -> acc.and(spec) }
            ?: Specification.where(null) // If no filters, return all

        return cardRepository.findAll(finalSpecification, pageable)
    }

    // New service methods to fetch data for filter dropdowns
    fun getAllBrands(manufacturerId: Long? = null): List<CardBrand> {
        return if (manufacturerId != null) {
            cardBrandRepository.findAllByManufacturerIdOrderByNameAsc(manufacturerId)
        } else {
            cardBrandRepository.findAllByOrderByNameAsc()
        }
    }

    fun getAllThemes(manufacturerId: Long? = null, brandId: Long? = null): List<CardTheme> {
        return when {
            brandId != null -> cardThemeRepository.findAllByBrandIdOrderByNameAsc(brandId)
            manufacturerId != null -> cardThemeRepository.findAllByBrandManufacturerIdOrderByNameAsc(manufacturerId)
            else -> cardThemeRepository.findAllByOrderByNameAsc()
        }
    }

    fun getAllSports(): List<Sport> {
        return sportRepository.findAll()
    }

    fun getAllSeasons(): List<Season> {
        return seasonRepository.findAllByOrderByNameAsc()
    }

    fun getAllVariants(): List<Variant> {
        return variantRepository.findAll()
    }

    fun getAllTeams(): List<Team> { //This might need to be adjusted if we want it to be dynamic based on current filters
        return teamRepository.findAll()
    }
}

