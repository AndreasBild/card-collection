package de.maulmann.cardcollection.controller

import de.maulmann.cardcollection.service.CardManufacturerService
import de.maulmann.cardcollection.service.CardService
import de.maulmann.cardcollection.service.PlayerService // Added
import de.maulmann.cardcollection.service.PrintRunRange // Import PrintRunRange
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam // Added

@Controller
@RequestMapping("/cards")
class CardController(
    private val cardService: CardService,
    private val cardManufacturerService: CardManufacturerService,
    private val playerService: PlayerService // Added PlayerService
) {

    @GetMapping // This will be the main endpoint, effectively "/cards"
    fun getCards(
        model: Model,
        @RequestParam(required = false) manufacturerId: Long?,
        @RequestParam(required = false) brandId: Long?,
        @RequestParam(required = false) themeId: Long?,
        @RequestParam(required = false) sportId: Long?,
        @RequestParam(required = false) playerId: Long?,
        @RequestParam(required = false) seasonId: Long?, // Changed from season: String?
        @RequestParam(required = false) gameUsed: Boolean?, // New
        @RequestParam(required = false) autograph: Boolean?, // New
        @RequestParam(required = false) variantId: Long?,
        @RequestParam(required = false) rookieCard: Boolean?,
        @RequestParam(required = false) printRunRangeKey: String?, // New parameter
        @RequestParam(required = false) teamId: Long?, // New parameter
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(required = false) sort: String?
    ): String {
        val sortObj = sort?.let { it ->
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
                    Sort.by("id") // Fallback bei ungültigem Feld
                }
            }
        } ?: Sort.by("id")

        val pageable = PageRequest.of(page, size, sortObj)

        // Call the new service method that handles combined filtering
        val cardsPage: Page<de.maulmann.cardcollection.model.Card> = cardService.getCardsFiltered(
            manufacturerId = manufacturerId,
            brandId = brandId,
            themeId = themeId,
            sportId = sportId,
            playerId = playerId,
            seasonId = seasonId, // Changed from season = season
            gameUsed = gameUsed,
            autograph = autograph,
            variantId = variantId,
            rookieCard = rookieCard,
            printRunRangeKey = printRunRangeKey, // Pass new parameter
            teamId = teamId, // Pass new parameter
            pageable = pageable // Pass the pageable object
        )

        model.addAttribute("cardPage", cardsPage)
        model.addAttribute("cards", cardsPage.content) // For existing view compatibility
        model.addAttribute("currentPage", cardsPage.number)
        model.addAttribute("totalPages", cardsPage.totalPages)
        model.addAttribute("totalItems", cardsPage.totalElements)
        model.addAttribute("pageSize", cardsPage.size)


        // Fetch data for filters/dropdowns (this part remains the same)
        model.addAttribute("manufacturers", cardManufacturerService.getAllCardManufacturers())
        model.addAttribute("players", playerService.getPlayers()) // Assuming getPlayers() fetches List<Player>
        model.addAttribute("brands", cardService.getAllBrands())
        model.addAttribute("themes", cardService.getAllThemes())
        model.addAttribute("sports", cardService.getAllSports())
        model.addAttribute("seasons", cardService.getAllSeasons())
        model.addAttribute("variants", cardService.getAllVariants())
        model.addAttribute("printRunRanges", PrintRunRange.entries.toTypedArray())
        model.addAttribute("teams", cardService.getAllTeams()) // Add Teams to the model

        return "cards" // Returns the "cards.html" view
    }
}