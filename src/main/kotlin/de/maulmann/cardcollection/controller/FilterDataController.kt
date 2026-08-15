package de.maulmann.cardcollection.controller

import de.maulmann.cardcollection.model.CardBrand
import de.maulmann.cardcollection.model.CardTheme
import de.maulmann.cardcollection.model.Variant
import de.maulmann.cardcollection.service.CardService
import org.springframework.http.CacheControl
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.TimeUnit

@RestController
@RequestMapping("/api/filters")
class FilterDataController(private val cardService: CardService) {

    private val filterCacheControl = CacheControl.maxAge(1, TimeUnit.HOURS).cachePublic()

    @GetMapping("/brands")
    fun getBrandsForFilter(@RequestParam(required = false) manufacturerId: Long?): ResponseEntity<List<CardBrand>> {
        return ResponseEntity.ok()
            .cacheControl(filterCacheControl)
            .body(cardService.getAllBrands(manufacturerId))
    }

    @GetMapping("/themes")
    fun getThemesForFilter(
        @RequestParam(required = false) manufacturerId: Long?,
        @RequestParam(required = false) brandId: Long?
    ): ResponseEntity<List<CardTheme>> {
        return ResponseEntity.ok()
            .cacheControl(filterCacheControl)
            .body(cardService.getAllThemes(manufacturerId = manufacturerId, brandId = brandId))
    }

    @GetMapping("/variants")
    fun getVariantsForFilter(
        @RequestParam(required = false) manufacturerId: Long?,
        @RequestParam(required = false) brandId: Long?,
        @RequestParam(required = false) themeId: Long?
    ): ResponseEntity<List<Variant>> {
        return ResponseEntity.ok()
            .cacheControl(filterCacheControl)
            .body(cardService.getAllVariants(manufacturerId, brandId, themeId))
    }
}

