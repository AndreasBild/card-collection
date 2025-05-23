package de.maulmann.cardcollection.service

import de.maulmann.cardcollection.model.Card
import de.maulmann.cardcollection.model.CardBrand
import de.maulmann.cardcollection.model.CardTheme
import de.maulmann.cardcollection.model.Sport
import de.maulmann.cardcollection.model.Team // Import Team
import de.maulmann.cardcollection.model.Variant
import de.maulmann.cardcollection.repository.CardBrandRepository
import de.maulmann.cardcollection.repository.CardRepository
import de.maulmann.cardcollection.repository.CardThemeRepository
import de.maulmann.cardcollection.repository.SportRepository
import de.maulmann.cardcollection.repository.TeamRepository // Import TeamRepository
import de.maulmann.cardcollection.repository.VariantRepository
import de.maulmann.cardcollection.service.PrintRunRange // Import PrintRunRange
import org.springframework.stereotype.Service

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
    fun getCardsByManufacturerId(manufacturerId: Long): List<Card> {
        return cardRepository.findAllByThemeBrandManufacturerId(manufacturerId)
    }

    fun getCardsByBrandId(brandId: Long): List<Card> {
        return cardRepository.findAllByThemeBrandId(brandId)
    }

    fun getCardsByThemeId(themeId: Long): List<Card> {
        return cardRepository.findAllByThemeId(themeId)
    }

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
        teamId: Long? // New parameter
    ): List<Card> {
        // Initial approach: Fetch all cards and then filter iteratively.
        // This can be optimized later with JPA Specifications if performance becomes an issue.
        var filteredCards = cardRepository.findAll() // Start with all cards

        // Print Run Filtering Logic
        val selectedPrintRunRange = PrintRunRange.fromKey(printRunRangeKey)
        selectedPrintRunRange?.let { range ->
            filteredCards = when (range) {
                PrintRunRange.ONE -> filteredCards.filter { card -> card.printRun == 1 }
                PrintRunRange.LE_10 -> filteredCards.filter { card -> card.printRun > 0 && card.printRun <= 10 }
                PrintRunRange.LE_50 -> filteredCards.filter { card -> card.printRun > 0 && card.printRun <= 50 }
                PrintRunRange.LE_100 -> filteredCards.filter { card -> card.printRun > 0 && card.printRun <= 100 }
                PrintRunRange.LE_500 -> filteredCards.filter { card -> card.printRun > 0 && card.printRun <= 500 }
                PrintRunRange.LE_1000 -> filteredCards.filter { card -> card.printRun > 0 && card.printRun <= 1000 }
                PrintRunRange.ALL_WITH_PRINT_RUN -> filteredCards.filter { card -> card.printRun > 0 }
            }
        }

        manufacturerId?.let {
            filteredCards = filteredCards.filter { card -> card.theme.brand.manufacturer.id == it }
        }
        brandId?.let {
            filteredCards = filteredCards.filter { card -> card.theme.brand.id == it }
        }
        themeId?.let {
            filteredCards = filteredCards.filter { card -> card.theme.id == it }
        }
        sportId?.let {
            filteredCards = filteredCards.filter { card -> card.player.sport.id == it }
        }
        playerId?.let {
            filteredCards = filteredCards.filter { card -> card.player.id == it }
        }
        season?.takeIf { it.isNotBlank() }?.let {
            filteredCards = filteredCards.filter { card -> card.season == it }
        }
        gameUsed?.let {
            filteredCards = filteredCards.filter { card -> card.gameUsedMaterial == it }
        }
        autograph?.let {
            filteredCards = filteredCards.filter { card -> card.autograph == it }
        }

        variantId?.let { vId ->
            filteredCards = filteredCards.filter { card -> card.variant.id == vId }
        }

        rookieCard?.let { isRookie ->
            filteredCards = filteredCards.filter { card -> card.rookieCard == isRookie }
        }

        teamId?.let { tId ->
            filteredCards = filteredCards.filter { card -> card.player.team.id == tId }
        }
        return filteredCards
    }

    // New service methods to fetch data for filter dropdowns
    fun getAllBrands(): List<CardBrand> {
        return cardBrandRepository.findAll()
    }

    fun getAllThemes(): List<CardTheme> {
        return cardThemeRepository.findAll()
    }

    fun getAllSports(): List<Sport> {
        return sportRepository.findAll()
    }

    fun getAllSeasons(): List<String> {
        return cardRepository.findDistinctSeasons()
    }

    fun getAllVariants(): List<Variant> {
        return variantRepository.findAll()
    }

    fun getAllTeams(): List<Team> {
        return teamRepository.findAll()
    }
}

