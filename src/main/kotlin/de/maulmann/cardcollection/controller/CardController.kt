package de.maulmann.cardcollection.controller

import de.maulmann.cardcollection.model.Player // Added
import de.maulmann.cardcollection.service.CardManufacturerService
import de.maulmann.cardcollection.service.CardService
import de.maulmann.cardcollection.service.PlayerService // Added
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
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
        @RequestParam(required = false) season: String?
    ): String {
        val cards = if (manufacturerId != null) {
            cardService.getCardsByManufacturerId(manufacturerId)
        } else if (brandId != null) {
            cardService.getCardsByBrandId(brandId)
        } else if (themeId != null) {
            cardService.getCardsByThemeId(themeId)
        } else if (sportId != null) {
            cardService.getCardsBySportId(sportId)
        } else if (playerId != null) {
            cardService.findAllByPlayerId(playerId) // Assuming this returns List<Card>
        } else if (season != null && season.isNotBlank()) {
            cardService.getCardsBySeason(season)
        } else {
            cardService.getAllCards()
        }
        model.addAttribute("cards", cards)

        // Fetch data for filters/dropdowns
        model.addAttribute("manufacturers", cardManufacturerService.getAllCardManufacturers())
        model.addAttribute("players", playerService.getPlayers()) // Assuming getPlayers() fetches List<Player>
        model.addAttribute("brands", cardService.getAllBrands())
        model.addAttribute("themes", cardService.getAllThemes())
        model.addAttribute("sports", cardService.getAllSports())
        model.addAttribute("seasons", cardService.getAllSeasons())

        return "cards" // Returns the "cards.html" view
    }

    @GetMapping("/{id}") // Path relative to "/cards"
    fun getCardById(@PathVariable id: Long, model: Model): String {
        // Assuming cardService.findAllById returns a List<Card> for consistency with the view.
        // If it's meant to be a single card, the view or model attribute might need adjustment.
        // Based on previous structure, it added a list to the "cards" attribute.
        model.addAttribute("cards", cardService.findAllById(id)) // Corrected service method name
        return "cards"
    }

    @GetMapping("/run/{printRunValue}") // Path relative to "/cards", made path variable more descriptive
    fun findAllByPrintRunIsLessThan(@PathVariable printRunValue: Int, model: Model): String {
        model.addAttribute("cards", cardService.findAllByPrintRunIsLessThan(printRunValue))
        return "cards"
    }

    @GetMapping("/rookie") // Path relative to "/cards"
    fun findAllRookieCards(model: Model): String {
        // Changed model attribute to "cards" and view to "cards" for consistency
        model.addAttribute("cards", cardService.findAllByRookieCard(rookieCard = true))
        return "cards"
    }

    // Endpoint for cards by manufacturer (from previous outer controller's functionality, if needed explicitly)
    // This might be handled by the main getCards with parameters later.
    // For now, we provide a specific endpoint if direct access to cards by manufacturer is desired.
    @GetMapping("/byManufacturer/{manufacturerId}")
    fun getCardsByManufacturer(@PathVariable manufacturerId: Long, model: Model): String {
        model.addAttribute("cards", cardManufacturerService.getCardsByManufacturerId(manufacturerId))
        // Add filter data as well, for consistency if user navigates from here
        model.addAttribute("manufacturers", cardManufacturerService.getAllCardManufacturers())
        model.addAttribute("players", playerService.getPlayers())
        model.addAttribute("brands", cardService.getAllBrands())
        model.addAttribute("themes", cardService.getAllThemes())
        model.addAttribute("sports", cardService.getAllSports())
        model.addAttribute("seasons", cardService.getAllSeasons())
        return "cards"
    }
}
