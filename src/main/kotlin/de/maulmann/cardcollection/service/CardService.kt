package de.maulmann.cardcollection.service

import de.maulmann.cardcollection.model.Card
import de.maulmann.cardcollection.model.CardBrand
import de.maulmann.cardcollection.model.CardTheme
import de.maulmann.cardcollection.model.Player
import de.maulmann.cardcollection.model.Sport
import de.maulmann.cardcollection.model.Team // Import Team
import de.maulmann.cardcollection.model.CardManufacturer
import de.maulmann.cardcollection.model.Variant
import de.maulmann.cardcollection.repository.CardBrandRepository
import de.maulmann.cardcollection.repository.CardRepository
import de.maulmann.cardcollection.repository.CardThemeRepository
import de.maulmann.cardcollection.repository.SportRepository
import de.maulmann.cardcollection.repository.TeamRepository // Import TeamRepository
import de.maulmann.cardcollection.repository.VariantRepository
import de.maulmann.cardcollection.service.PrintRunRange // Import PrintRunRange
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import jakarta.persistence.criteria.Predicate


@Service
class CardService(
    private val cardRepository: CardRepository,
    private val cardBrandRepository: CardBrandRepository,
    private val cardThemeRepository: CardThemeRepository,
    private val sportRepository: SportRepository,
    private val variantRepository: VariantRepository,
    private val teamRepository: TeamRepository // New injection
) {

    fun getAllCards(): List<Card> = cardRepository.findAll()

    fun findAllByRookieCard(rookieCard: Boolean): List<Card> {
        return cardRepository.findAllByRookieCard(rookieCard)
    }

    fun findAllByPlayerId(id: Long): List<Card> {
        return cardRepository.findAllByPlayerId(id)
    }

    // Assuming findAllByRookieCardId was a typo and meant to be something else or is redundant.
    // If it was intended to be distinct from findAllByPlayerId, its logic needs clarification.
    // For now, I'll keep it calling findAllByPlayerId but with the corrected return type.
    // Or remove if it's a duplicate of findAllByPlayerId.
    // Based on current implementation, it's identical to findAllByPlayerId.
    // Let's assume it was a copy-paste error and remove it for now to avoid confusion.
    // fun findAllByRookieCardId(id: Long): List<Card> {
    //     return cardRepository.findAllByPlayerId(id)
    // }

    fun findAllById(id: Long): List<Card> { // Corrected method name
        return cardRepository.findAllById(id)
    }

    fun findAllByAutograph(autograph: Boolean): List<Card> {
        return cardRepository.findAllByAutograph(autograph)
    }

    fun findAllByGameUsedMaterial(gameUsedMaterial: Boolean): List<Card> {
        return cardRepository.findAllByGameUsedMaterial(gameUsedMaterial)
    }

    fun getCardById(id: Long): Card? = cardRepository.findById(id).orElse(null)

    // New service methods for filtering cards
    fun getCardsBySportId(sportId: Long): List<Card> {
        return cardRepository.findAllByPlayerSportId(sportId)
    }

    fun getCardsBySeason(season: String): List<Card> {
        return cardRepository.findAllBySeason(season)
    }

    fun getCardsFiltered(
        manufacturerId: Long?,
        brandId: Long?,
        themeId: Long?,
        sportId: Long?,
        playerId: Long?,
        season: String?,
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
        season?.takeIf { it.isNotBlank() }?.let {
            specifications.add(Specification { root, _, cb ->
                cb.equal(root.get<String>("season"), it)
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

    fun getAllSeasons(): List<String> { //This might need to be adjusted if we want it to be dynamic based on current filters
        return cardRepository.findDistinctSeasons()
    }

    fun getAllVariants(): List<Variant> {
        return variantRepository.findAll()
    }

    fun getAllTeams(): List<Team> { //This might need to be adjusted if we want it to be dynamic based on current filters
        return teamRepository.findAll()
    }
}

