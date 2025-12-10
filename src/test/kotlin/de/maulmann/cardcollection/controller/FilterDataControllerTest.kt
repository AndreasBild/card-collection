package de.maulmann.cardcollection.controller

import de.maulmann.cardcollection.model.CardBrand
import de.maulmann.cardcollection.model.CardManufacturer
import de.maulmann.cardcollection.model.CardTheme
import de.maulmann.cardcollection.service.CardService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@AutoConfigureMockMvc
class FilterDataControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var cardService: CardService

    @Test
    fun `getBrandsForFilter should return all brands when no manufacturerId is provided`() {
        val manufacturer = CardManufacturer(name = "Panini")
        val brands = listOf(CardBrand(name = "Brand A", manufacturer = manufacturer), CardBrand(name = "Brand B", manufacturer = manufacturer))
        whenever(cardService.getAllBrands(null)).thenReturn(brands)

        mockMvc.perform(get("/api/filters/brands"))
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].name").value("Brand A"))
    }

    @Test
    fun `getBrandsForFilter should return filtered brands when manufacturerId is provided`() {
        val manufacturerId = 1L
        val manufacturer = CardManufacturer(name = "Panini")
        val brands = listOf(CardBrand(name = "Brand A", manufacturer = manufacturer))
        whenever(cardService.getAllBrands(manufacturerId)).thenReturn(brands)

        mockMvc.perform(get("/api/filters/brands").param("manufacturerId", manufacturerId.toString()))
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].name").value("Brand A"))
    }

    @Test
    fun `getThemesForFilter should return all themes when no parameters are provided`() {
        val manufacturer = CardManufacturer(name = "Panini")
        val brand = CardBrand(name = "Prizm", manufacturer = manufacturer)
        val themes = listOf(CardTheme(name = "Theme X", brand = brand), CardTheme(name = "Theme Y", brand = brand))
        whenever(cardService.getAllThemes(null, null)).thenReturn(themes)

        mockMvc.perform(get("/api/filters/themes"))
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].name").value("Theme X"))
    }

    @Test
    fun `getThemesForFilter should return themes filtered by manufacturerId`() {
        val manufacturerId = 1L
        val manufacturer = CardManufacturer(name = "Panini")
        val brand = CardBrand(name = "Prizm", manufacturer = manufacturer)
        val themes = listOf(CardTheme(name = "Theme X", brand = brand))
        whenever(cardService.getAllThemes(manufacturerId, null)).thenReturn(themes)

        mockMvc.perform(get("/api/filters/themes").param("manufacturerId", manufacturerId.toString()))
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].name").value("Theme X"))
    }

    @Test
    fun `getThemesForFilter should return themes filtered by brandId`() {
        val brandId = 2L
        val manufacturer = CardManufacturer(name = "Panini")
        val brand = CardBrand(name = "Prizm", manufacturer = manufacturer)
        val themes = listOf(CardTheme(name = "Theme Y", brand = brand))
        whenever(cardService.getAllThemes(null, brandId)).thenReturn(themes)

        mockMvc.perform(get("/api/filters/themes").param("brandId", brandId.toString()))
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].name").value("Theme Y"))
    }

    @Test
    fun `getThemesForFilter should return themes filtered by both manufacturerId and brandId`() {
        val manufacturerId = 1L
        val brandId = 1L
        val manufacturer = CardManufacturer(name = "Panini")
        val brand = CardBrand(name = "Prizm", manufacturer = manufacturer)
        val themes = listOf(CardTheme(name = "Theme Z", brand = brand))
        whenever(cardService.getAllThemes(manufacturerId, brandId)).thenReturn(themes)

        mockMvc.perform(get("/api/filters/themes")
            .param("manufacturerId", manufacturerId.toString())
            .param("brandId", brandId.toString())
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].name").value("Theme Z"))
    }
}
