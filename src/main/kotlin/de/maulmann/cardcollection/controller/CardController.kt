package de.maulmann.cardcollection.controller

import com.fasterxml.jackson.databind.ObjectMapper
import de.maulmann.cardcollection.dto.CardFilter
import de.maulmann.cardcollection.model.Card
import de.maulmann.cardcollection.model.GradingCompany
import de.maulmann.cardcollection.service.CardService
import de.maulmann.cardcollection.service.PlayerService
import de.maulmann.cardcollection.service.PrintRunRange
import de.maulmann.cardcollection.model.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.util.UriComponentsBuilder

@Controller
class CardController(
    private val cardService: CardService,
    private val playerService: PlayerService
) {

    private val objectMapper = ObjectMapper()

    companion object {
        data class SortableColumnInfo(val displayName: String, val propertyPath: String, val isSortable: Boolean = true)
        data class ActiveFilterChip(val label: String, val value: String, val removeUrl: String)

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
            SortableColumnInfo("Ratio", "packOdds"),
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

        val manufacturers = cardService.getAllCardManufacturers()
        val players = playerService.getPlayers()
        val brands = cardService.getAllBrands()
        val themes = cardService.getAllThemes()
        val sports = cardService.getAllSports()
        val seasons = cardService.getAllSeasons()
        val variants = cardService.getAllVariants()
        val teams = cardService.getAllTeams()

        val activeFilterChips = buildActiveFilterChips(
            filter = filter,
            manufacturers = manufacturers,
            brands = brands,
            themes = themes,
            sports = sports,
            players = players,
            teams = teams,
            seasons = seasons,
            variants = variants,
            size = size,
            sort = sort
        )

        model.addAttribute("manufacturers", manufacturers)
        model.addAttribute("players", players)
        model.addAttribute("brands", brands)
        model.addAttribute("themes", themes)
        model.addAttribute("sports", sports)
        model.addAttribute("seasons", seasons)
        model.addAttribute("variants", variants)
        model.addAttribute("teams", teams)
        model.addAttribute("printRunRanges", PrintRunRange.entries.toTypedArray())
        model.addAttribute("gradingCompanies", GradingCompany.entries)
        model.addAttribute("activeFilterChips", activeFilterChips)
        model.addAttribute("jsonLdSchema", buildJsonLd(cardsPage.content, cardsPage.totalElements))

        return "cards"
    }

    private fun buildActiveFilterChips(
        filter: CardFilter,
        manufacturers: List<CardManufacturer>,
        brands: List<CardBrand>,
        themes: List<CardTheme>,
        sports: List<Sport>,
        players: List<Player>,
        teams: List<Team>,
        seasons: List<Season>,
        variants: List<Variant>,
        size: String,
        sort: String?
    ): List<ActiveFilterChip> {
        val chips = mutableListOf<ActiveFilterChip>()

        fun urlWithout(paramKey: String): String {
            val builder = UriComponentsBuilder.fromPath("/cards")
            if (size != "20") builder.queryParam("size", size)
            if (!sort.isNullOrBlank()) builder.queryParam("sort", sort)

            if (paramKey != "manufacturerId" && filter.manufacturerId != null) builder.queryParam("manufacturerId", filter.manufacturerId)
            if (paramKey != "brandId" && filter.brandId != null) builder.queryParam("brandId", filter.brandId)
            if (paramKey != "themeId" && filter.themeId != null) builder.queryParam("themeId", filter.themeId)
            if (paramKey != "sportId" && filter.sportId != null) builder.queryParam("sportId", filter.sportId)
            if (paramKey != "playerId" && filter.playerId != null) builder.queryParam("playerId", filter.playerId)
            if (paramKey != "teamId" && filter.teamId != null) builder.queryParam("teamId", filter.teamId)
            if (paramKey != "seasonId" && filter.seasonId != null) builder.queryParam("seasonId", filter.seasonId)
            if (paramKey != "variantId" && filter.variantId != null) builder.queryParam("variantId", filter.variantId)
            if (paramKey != "gameUsed" && filter.gameUsed != null) builder.queryParam("gameUsed", filter.gameUsed)
            if (paramKey != "autograph" && filter.autograph != null) builder.queryParam("autograph", filter.autograph)
            if (paramKey != "rookieCard" && filter.rookieCard != null) builder.queryParam("rookieCard", filter.rookieCard)
            if (paramKey != "printRunRangeKey" && !filter.printRunRangeKey.isNullOrBlank()) builder.queryParam("printRunRangeKey", filter.printRunRangeKey)
            if (paramKey != "isGradedNullable" && filter.isGradedNullable != null) builder.queryParam("isGradedNullable", filter.isGradedNullable)

            return builder.build().toUriString()
        }

        filter.manufacturerId?.let { id ->
            val name = manufacturers.find { it.id == id }?.name ?: id.toString()
            chips.add(ActiveFilterChip("Manufacturer", name, urlWithout("manufacturerId")))
        }
        filter.brandId?.let { id ->
            val name = brands.find { it.id == id }?.name ?: id.toString()
            chips.add(ActiveFilterChip("Brand", name, urlWithout("brandId")))
        }
        filter.themeId?.let { id ->
            val name = themes.find { it.id == id }?.name ?: id.toString()
            chips.add(ActiveFilterChip("Theme", name, urlWithout("themeId")))
        }
        filter.sportId?.let { id ->
            val name = sports.find { it.id == id }?.name ?: id.toString()
            chips.add(ActiveFilterChip("Sport", name, urlWithout("sportId")))
        }
        filter.playerId?.let { id ->
            val name = players.find { it.id == id }?.let { "${it.name} ${it.surname}".trim() } ?: id.toString()
            chips.add(ActiveFilterChip("Player", name, urlWithout("playerId")))
        }
        filter.teamId?.let { id ->
            val name = teams.find { it.id == id }?.name ?: id.toString()
            chips.add(ActiveFilterChip("Team", name, urlWithout("teamId")))
        }
        filter.seasonId?.let { id ->
            val name = seasons.find { it.id == id }?.name ?: id.toString()
            chips.add(ActiveFilterChip("Season", name, urlWithout("seasonId")))
        }
        filter.variantId?.let { id ->
            val name = variants.find { it.id == id }?.name ?: id.toString()
            chips.add(ActiveFilterChip("Variant", name, urlWithout("variantId")))
        }
        filter.gameUsed?.let {
            chips.add(ActiveFilterChip("Game Used", if (it) "Yes" else "No", urlWithout("gameUsed")))
        }
        filter.autograph?.let {
            chips.add(ActiveFilterChip("Autograph", if (it) "Yes" else "No", urlWithout("autograph")))
        }
        filter.rookieCard?.let {
            chips.add(ActiveFilterChip("Rookie", if (it) "Yes" else "No", urlWithout("rookieCard")))
        }
        filter.printRunRangeKey?.takeIf { it.isNotBlank() }?.let { key ->
            val displayName = PrintRunRange.fromKey(key)?.displayName ?: key
            chips.add(ActiveFilterChip("Print Run", displayName, urlWithout("printRunRangeKey")))
        }
        filter.isGradedNullable?.let {
            chips.add(ActiveFilterChip("Graded", if (it) "Yes" else "No", urlWithout("isGradedNullable")))
        }

        return chips
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