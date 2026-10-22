package de.maulmann.cardcollection.service

import de.maulmann.cardcollection.dto.CardFilter
import de.maulmann.cardcollection.model.*
import de.maulmann.cardcollection.repository.*
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
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

    @Cacheable("filteredCards", key = "{#filter, #pageable.pageNumber, #pageable.pageSize, #pageable.sort}")
    fun getCardsFiltered(
        filter: CardFilter,
        pageable: Pageable
    ): Page<Card> {
        val cardsPage = cardRepository.findAll(CardSpecifications.fromFilter(filter), pageable)
        // The page query cannot fetch-join the cardPlayers collection (pagination would happen in memory),
        // so initialise players, sports and teams for exactly this page with one explicit query.
        cardsPage.content.map { it.id }.chunked(PEOPLE_FETCH_CHUNK_SIZE).forEach { ids ->
            cardRepository.fetchPeopleByIds(ids)
        }
        return cardsPage
    }

    private companion object {
        const val PEOPLE_FETCH_CHUNK_SIZE = 1000
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