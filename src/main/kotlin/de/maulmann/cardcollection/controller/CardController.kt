package de.maulmann.cardcollection.controller

import com.fasterxml.jackson.databind.ObjectMapper
import de.maulmann.cardcollection.dto.CardFilter
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
import org.springframework.web.bind.annotation.RequestParam

@Controller
class CardController(
    private val cardService: CardService,
    private val playerService: PlayerService
) {

    private val objectMapper = ObjectMapper()

    companion object {
        data class SortableColumnInfo(val displayName: String, val propertyPath: String, val isSortable: Boolean = true)

        private val SORTABLE_COLUMNS = listOf(
            SortableColumnInfo("Player", "playerNames", false),
            SortableColumnInfo("Team", "teamNames", false),
            SortableColumnInfo("Sport", "sportNames", false),
            SortableColumnInfo("Season", "season.name"),
            SortableColumnInfo("Company", "manufacturer.name"),
            SortableColumnInfo("Brand", "brand.name"),
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

    @GetMapping(value = ["", "/", "/cards"])
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
        @RequestParam(defaultValue = "20") size: String,
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

        val isAll = size.equals("all", ignoreCase = true)
        val parsedSize = size.toIntOrNull() ?: 20
        val pageSize = if (isAll) 100_000 else parsedSize.coerceAtLeast(1)
        val currentPageIndex = if (isAll) 0 else page

        val pageable = PageRequest.of(currentPageIndex, pageSize, sortObj)

        val filter = CardFilter(
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
            isGradedNullable = isGradedNullable
        )

        val cardsPage: Page<Card> = cardService.getCardsFiltered(filter, pageable)

        model.addAttribute("cardPage", cardsPage)
        model.addAttribute("cards", cardsPage.content)
        model.addAttribute("currentPage", cardsPage.number)
        model.addAttribute("totalPages", cardsPage.totalPages)
        model.addAttribute("totalItems", cardsPage.totalElements)
        model.addAttribute("pageSize", if (isAll) "all" else parsedSize.toString())
        model.addAttribute("isAllSize", isAll)

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
        model.addAttribute("jsonLdSchema", buildJsonLd(cardsPage.content, cardsPage.totalElements))

        return "cards"
    }

    private fun buildJsonLd(cards: List<Card>, totalItems: Long): String {
        val items = cards.mapIndexed { index, card ->
            val cardTitle = listOfNotNull(
                card.season.name,
                card.brand.name,
                card.theme.name,
                card.variant.name,
                card.playerNames,
                card.number.takeIf { it.isNotBlank() }?.let { "#$it" }
            ).filter { it.isNotBlank() }.joinToString(" ")

            mapOf(
                "@type" to "ListItem",
                "position" to (index + 1),
                "item" to mapOf(
                    "@type" to "Product",
                    "name" to cardTitle,
                    "category" to "Sports Memorabilia > Trading Cards",
                    "brand" to mapOf(
                        "@type" to "Brand",
                        "name" to card.brand.name.ifBlank { "Trading Card" }
                    ),
                    "manufacturer" to mapOf(
                        "@type" to "Organization",
                        "name" to card.manufacturer.name.ifBlank { "Manufacturer" }
                    )
                )
            )
        }

        val schemaMap = mapOf(
            "@context" to "https://schema.org",
            "@type" to "CollectionPage",
            "name" to "Juwan Howard Basketball Trading Card Collection",
            "description" to "Private Collection of Juwan Howard Basketball Trading Cards containing rare cards from Panini, Fleer, Topps, and Upper Deck.",
            "about" to mapOf(
                "@type" to "Person",
                "name" to "Juwan Howard",
                "jobTitle" to "Basketball Player"
            ),
            "mainEntity" to mapOf(
                "@type" to "ItemList",
                "numberOfItems" to totalItems,
                "itemListElement" to items
            )
        )

        return try {
            objectMapper.writeValueAsString(schemaMap)
        } catch (e: Exception) {
            "{}"
        }
    }
}