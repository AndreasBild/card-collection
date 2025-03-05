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
