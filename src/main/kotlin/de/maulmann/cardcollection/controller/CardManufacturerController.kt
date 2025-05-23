package de.maulmann.cardcollection.controller

import de.maulmann.cardcollection.model.Card
import de.maulmann.cardcollection.service.CardManufacturerService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController



@RestController
@RequestMapping("/api/cards/manufacturer")
class CardManufacturerController(private val cardManufacturerService: CardManufacturerService) {

    @GetMapping("/{manufacturerId}")
    fun getCardsByManufacturerId(@PathVariable manufacturerId: Long): List<Card> {
        return cardManufacturerService.getCardsByManufacturerId(manufacturerId)
    }
}