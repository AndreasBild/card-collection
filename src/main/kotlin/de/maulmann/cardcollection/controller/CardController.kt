package de.maulmann.cardcollection.controller

import de.maulmann.cardcollection.model.Player // Added
import de.maulmann.cardcollection.service.CardManufacturerService
import de.maulmann.cardcollection.service.CardService
import de.maulmann.cardcollection.service.PlayerService // Added
import de.maulmann.cardcollection.service.PrintRunRange // Import PrintRunRange
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
        @RequestParam(required = false) season: String?,
        @RequestParam(required = false) gameUsed: Boolean?, // New
        @RequestParam(required = false) autograph: Boolean?, // New
        @RequestParam(required = false) variantId: Long?,
        @RequestParam(required = false) rookieCard: Boolean?,
        @RequestParam(required = false) printRunRangeKey: String?, // New parameter
        @RequestParam(required = false) teamId: Long? // New parameter
    ): String {
        // Call the new service method that handles combined filtering
        val cards = cardService.getCardsFiltered(
            manufacturerId = manufacturerId,
            brandId = brandId,
            themeId = themeId,
            sportId = sportId,
            playerId = playerId,
            season = season,
            gameUsed = gameUsed,
            autograph = autograph,
            variantId = variantId,
            rookieCard = rookieCard,
            printRunRangeKey = printRunRangeKey, // Pass new parameter
            teamId = teamId // Pass new parameter
        )
        model.addAttribute("cards", cards)

        // Fetch data for filters/dropdowns (this part remains the same)
        model.addAttribute("manufacturers", cardManufacturerService.getAllCardManufacturers())
        model.addAttribute("players", playerService.getPlayers()) // Assuming getPlayers() fetches List<Player>
        model.addAttribute("brands", cardService.getAllBrands())
        model.addAttribute("themes", cardService.getAllThemes())
        model.addAttribute("sports", cardService.getAllSports())
        model.addAttribute("seasons", cardService.getAllSeasons())
        model.addAttribute("variants", cardService.getAllVariants())
        
        model.addAttribute("printRunRanges", PrintRunRange.entries.toTypedArray())
        model.addAttribute("teams", cardService.getAllTeams()) // Add Teams to model

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


    @GetMapping("/rookie") // Path relative to "/cards"
    fun findAllRookieCards(model: Model): String {
        // Changed model attribute to "cards" and view to "cards" for consistency
        model.addAttribute("cards", cardService.findAllByRookieCard(rookieCard = true))
        return "cards"
    }

}
