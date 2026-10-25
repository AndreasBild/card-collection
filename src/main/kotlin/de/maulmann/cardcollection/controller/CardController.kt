package de.maulmann.cardcollection.controller

import de.maulmann.cardcollection.dto.CardFilter
import de.maulmann.cardcollection.dto.CardListItem
import de.maulmann.cardcollection.dto.CardsUrlBuilder
import de.maulmann.cardcollection.model.FeaturedPlayer
import de.maulmann.cardcollection.model.GradingCompany
import de.maulmann.cardcollection.service.CardService
import de.maulmann.cardcollection.service.PlayerService
import de.maulmann.cardcollection.service.PrintRunRange
import de.maulmann.cardcollection.service.ReferenceDataService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class CardController(
    private val cardService: CardService,
    private val referenceDataService: ReferenceDataService,
    private val playerService: PlayerService
) {

    private companion object {
        const val ALL_PAGE_SIZE = 10_000
        const val DEFAULT_PAGE_SIZE = 20
        const val MAX_PAGE_SIZE = 100
    }

    @GetMapping(value = ["", "/", "/cards"])
    fun getCards(
        model: Model,
        filter: CardFilter,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "$DEFAULT_PAGE_SIZE") size: String,
        @RequestParam(required = false) sort: String?
    ): String {
        val listSort = CardListSort.parse(sort)
        listSort.error?.let { model.addAttribute("error", it) }

        val isAll = size.equals("all", ignoreCase = true)
        val parsedSize = size.toIntOrNull() ?: DEFAULT_PAGE_SIZE
        val pageSize = if (isAll) ALL_PAGE_SIZE else parsedSize.coerceIn(1, MAX_PAGE_SIZE)
        val currentPageIndex = if (isAll) 0 else page.coerceAtLeast(0)
        val pageSizeLabel = if (isAll) "all" else parsedSize.toString()

        val cardsPage: Page<CardListItem> =
            cardService.getCardsFiltered(filter, PageRequest.of(currentPageIndex, pageSize, listSort.sort))

        model.addAttribute("ownerName", FeaturedPlayer.name)
        model.addAttribute("cardUrls", CardsUrlBuilder(filter, pageSizeLabel, sort))
        model.addAttribute("cardPage", cardsPage)
        model.addAttribute("cards", cardsPage.content)
        model.addAttribute("currentPage", cardsPage.number)
        model.addAttribute("totalPages", cardsPage.totalPages)
        model.addAttribute("totalItems", cardsPage.totalElements)
        model.addAttribute("pageSize", pageSizeLabel)
        model.addAttribute("isAllSize", isAll)
        model.addAttribute("currentSortProperty", listSort.currentProperty)
        model.addAttribute("currentSortDirection", listSort.currentDirection)
        model.addAttribute("sortableColumns", CardListSort.COLUMNS)

        val lookups = FilterLookups(
            manufacturers = referenceDataService.getAllCardManufacturers(),
            brands = referenceDataService.getAllBrands(),
            themes = referenceDataService.getAllThemes(),
            sports = referenceDataService.getAllSports(),
            players = playerService.getPlayers(),
            teams = referenceDataService.getAllTeams(),
            seasons = referenceDataService.getAllSeasons(),
            variants = referenceDataService.getAllVariants()
        )

        model.addAttribute("manufacturers", lookups.manufacturers)
        model.addAttribute("players", lookups.players)
        model.addAttribute("brands", lookups.brands)
        model.addAttribute("themes", lookups.themes)
        model.addAttribute("sports", lookups.sports)
        model.addAttribute("seasons", lookups.seasons)
        model.addAttribute("variants", lookups.variants)
        model.addAttribute("teams", lookups.teams)
        model.addAttribute("printRunRanges", PrintRunRange.entries.toTypedArray())
        model.addAttribute("gradingCompanies", GradingCompany.entries)
        model.addAttribute("activeFilterChips", ActiveFilterChips.build(filter, lookups, size, sort))
        model.addAttribute("jsonLdSchema", CardListJsonLd.build(cardsPage.content, cardsPage.totalElements))

        return "cards"
    }
}
