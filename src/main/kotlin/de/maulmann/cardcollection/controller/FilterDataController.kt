package de.maulmann.cardcollection.controller

import de.maulmann.cardcollection.model.CardBrand
import de.maulmann.cardcollection.model.CardTheme
import de.maulmann.cardcollection.model.Variant
import de.maulmann.cardcollection.service.CardService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/filters")
class FilterDataController(private val cardService: CardService) {

    @GetMapping("/brands")
    fun getBrandsForFilter(@RequestParam(required = false) manufacturerId: Long?): List<CardBrand> {
        return cardService.getAllBrands(manufacturerId)
    }

    @GetMapping("/themes")
    fun getThemesForFilter(
        @RequestParam(required = false) manufacturerId: Long?,
        @RequestParam(required = false) brandId: Long?
    ): List<CardTheme> {
        return cardService.getAllThemes(manufacturerId = manufacturerId, brandId = brandId)
    }

    @GetMapping("/variants")
    fun getVariantsForFilter(
        @RequestParam(required = false) manufacturerId: Long?,
        @RequestParam(required = false) brandId: Long?,
        @RequestParam(required = false) themeId: Long?
    ): List<Variant> {
        return cardService.getAllVariants(manufacturerId, brandId, themeId)
    }
}
