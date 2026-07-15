package de.maulmann.cardcollection.controller

import de.maulmann.cardcollection.model.Card
import de.maulmann.cardcollection.model.GradingCompany
import de.maulmann.cardcollection.service.CardService
import de.maulmann.cardcollection.service.PlayerService
import de.maulmann.cardcollection.service.PrintRunRange
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/cards")
class CardController(
    private val cardService: CardService,
    private val playerService: PlayerService
) {

    companion object {
        data class SortableColumnInfo(val displayName: String, val propertyPath: String, val isSortable: Boolean = true)

        private val SORTABLE_COLUMNS = listOf(
            SortableColumnInfo("Player", "playerNames", false),
            SortableColumnInfo("Team", "teamNames", false),
            SortableColumnInfo("Sport", "sportNames", false),
            SortableColumnInfo("Season", "season.name"),
            SortableColumnInfo("Company", "theme.brand.manufacturer.name"),
            SortableColumnInfo("Brand", "theme.brand.name"),
            SortableColumnInfo("Theme", "theme.name"),
            SortableColumnInfo("Variant", "variant.name"),
            SortableColumnInfo("Number", "number"),
            SortableColumnInfo("Serial", "serialNumber"),
            SortableColumnInfo("Print Run", "printRun"),
            SortableColumnInfo("Rookie", "rookieCard"),
            SortableColumnInfo("Game Used", "gameUsedMaterial"),
            SortableColumnInfo("Autograph", "autograph"),
            SortableColumnInfo("Grading Co.", "grading.gradingCompany"),
            SortableColumnInfo("Grade", "grading.grade")
        )
    }

    @GetMapping
    fun getCards(
        model: Model,
        @RequestParam(required = false) manufacturerId: Long?,
        @RequestParam(required = false) brandId: Long?,
        @RequestParam(required = false) themeId: Long?,
        @RequestParam(required = false) sportId: Long?,
        @RequestParam(required = false) playerId: Long?,
        @RequestParam(required = false) seasonId: Long?,
        @RequestParam(required = false) gameUsed: Boolean?,
        @RequestParam(required = false) autograph: Boolean?,
        @RequestParam(required = false) variantId: Long?,
        @RequestParam(required = false) rookieCard: Boolean?,
        @RequestParam(required = false) printRunRangeKey: String?,
        @RequestParam(required = false) teamId: Long?,
        @RequestParam(required = false) isGradedNullable: Boolean?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(required = false) sort: String?
    ): String {
        val sortObj = sort?.let {
            val parts = it.split(",").map { it.trim() }
            if (parts.isEmpty()) {
                Sort.by("id")
            } else {
                val field = parts[0].takeIf { it.isNotBlank() } ?: "id"
                val direction = when {
                    parts.size > 1 && parts[1].matches(Regex("desc|DESC|DESCENDING", RegexOption.IGNORE_CASE)) -> Sort.Direction.DESC
                    else -> Sort.Direction.ASC
                }
                try {
                    Sort.by(direction, field)
                } catch (e: Exception) {
                    e.message?.let { model.addAttribute("error", it) }
                    Sort.by("id")
                }
            }
        } ?: Sort.by("id")

        val pageable = PageRequest.of(page, size, sortObj)

        val cardsPage: Page<Card> = cardService.getCardsFiltered(
            manufacturerId = manufacturerId,
            brandId = brandId,
            themeId = themeId,
            sportId = sportId,
            playerId = playerId,
            seasonId = seasonId,
            gameUsed = gameUsed,
            autograph = autograph,
            variantId = variantId,
            rookieCard = rookieCard,
            printRunRangeKey = printRunRangeKey,
            teamId = teamId,
            isGradedNullable = isGradedNullable,
            pageable = pageable
        )

        model.addAttribute("cardPage", cardsPage)
        model.addAttribute("cards", cardsPage.content)
        model.addAttribute("currentPage", cardsPage.number)
        model.addAttribute("totalPages", cardsPage.totalPages)
        model.addAttribute("totalItems", cardsPage.totalElements)
        model.addAttribute("pageSize", cardsPage.size)

        var currentSortProperty = "id"
        var currentSortDirection = "ASC"

        sort?.let {
            val parts = it.split(",").map { part -> part.trim() }
            if (parts.isNotEmpty()) {
                currentSortProperty = parts[0].takeIf { p -> p.isNotBlank() } ?: "id"
                if (parts.size > 1) {
                    currentSortDirection = if (parts[1].equals("DESC", ignoreCase = true)) "DESC" else "ASC"
                }
            }
        }
        model.addAttribute("currentSortProperty", currentSortProperty)
        model.addAttribute("currentSortDirection", currentSortDirection)
        model.addAttribute("sortableColumns", SORTABLE_COLUMNS)

        model.addAttribute("manufacturers", cardService.getAllCardManufacturers())
        model.addAttribute("players", playerService.getPlayers())
        model.addAttribute("brands", cardService.getAllBrands())
        model.addAttribute("themes", cardService.getAllThemes())
        model.addAttribute("sports", cardService.getAllSports())
        model.addAttribute("seasons", cardService.getAllSeasons())
        model.addAttribute("variants", cardService.getAllVariants())
        model.addAttribute("printRunRanges", PrintRunRange.entries.toTypedArray())
        model.addAttribute("teams", cardService.getAllTeams())
        model.addAttribute("gradingCompanies", GradingCompany.entries)

        return "cards"
    }
}