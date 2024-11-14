package de.maulmann.cardcollection.controller


import de.maulmann.cardcollection.service.CardService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class CardController(private val cardService: CardService) {

    @GetMapping("/cards")
    fun getAllCards(model: Model): String {
        model.addAttribute("cards", cardService.getAllCards())
        return "cards"
    }

    @GetMapping("/cards/{id}")
    fun getCardById(@PathVariable id: Long, model: Model): String {
        val card = cardService.getCardById(id)
        model.addAttribute("card", card)
        return "cards"
    }

    @GetMapping("/rookie")
    fun getAllRookieCards(model: Model): String {
        model.addAttribute("rookie", cardService.getAllRookieCards())
        return "rookie"
    }
}
