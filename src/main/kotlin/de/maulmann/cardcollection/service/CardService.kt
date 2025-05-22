package de.maulmann.cardcollection.service

import de.maulmann.cardcollection.model.Card
import de.maulmann.cardcollection.model.CardBrand
import de.maulmann.cardcollection.model.CardTheme
import de.maulmann.cardcollection.model.Sport
import de.maulmann.cardcollection.repository.CardBrandRepository
import de.maulmann.cardcollection.repository.CardRepository
import de.maulmann.cardcollection.repository.CardThemeRepository
import de.maulmann.cardcollection.repository.SportRepository
import org.springframework.stereotype.Service

@Service
class CardService(
    private val cardRepository: CardRepository,
    private val cardBrandRepository: CardBrandRepository,
    private val cardThemeRepository: CardThemeRepository,
    private val sportRepository: SportRepository
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

    fun findAllByPrintRunIsLessThan(printRunIsLessThan: Int): List<Card> {
        return cardRepository.findAllByPrintRunLessThanEqual(printRunIsLessThan)
    }

    fun findAllByAutograph(autograph: Boolean): List<Card> {
        return cardRepository.findAllByAutograph(autograph)
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
}

