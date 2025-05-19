package de.maulmann.cardcollection.controller


import de.maulmann.cardcollection.service.CardManufacturerService
import de.maulmann.cardcollection.service.CardService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping


@Controller
@RequestMapping("/cards/manufacturers")
class CardController(
    private val cardService: CardService,
    private val cardManufacturerService: CardManufacturerService
) {

    @GetMapping
    fun getCards(model: Model): String {
        val cards = cardService.getAllCards()
        model.addAttribute("cards", cards)

        val manufacturers = cardManufacturerService.getAllCardManufacturers() // Fetch manufacturers
        model.addAttribute("manufacturers", manufacturers) // Add manufacturers to model

        return "cards"
    }


    @Controller
    class CardController(private val cardService: CardService) {

        @GetMapping("/cards")
        fun getAllCards(model: Model): String {
            model.addAttribute("cards", cardService.getAllCards())
            return "cards"
        }

        @GetMapping("/run/{id}")
        fun findAllByPrintRunIsLessThan(@PathVariable id: Int, model: Model): String {
            model.addAttribute("cards", cardService.findAllByPrintRunIsLessThan(id))
            return "cards"
        }


        @GetMapping("/cards/{id}")
        fun getCardById(@PathVariable id: Long, model: Model): String {
            val card = cardService.findallById(id)
            model.addAttribute("cards", card)
            return "cards"
        }

        @GetMapping("/rookie")
        fun findAllRookieCards(model: Model): String {
            model.addAttribute("rookie", cardService.findAllByRookieCard(rookieCard = true))
            return "rookie"
        }


    }
}

