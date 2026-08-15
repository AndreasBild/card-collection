package de.maulmann.cardcollection.controller

import de.maulmann.cardcollection.model.CardBrand
import de.maulmann.cardcollection.model.CardTheme
import de.maulmann.cardcollection.model.Variant
import de.maulmann.cardcollection.service.CardService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@ExtendWith(MockitoExtension::class)
class FilterDataControllerTest {

    private lateinit var mockMvc: MockMvc

    @Mock
    private lateinit var cardService: CardService

    @InjectMocks
    private lateinit var filterDataController: FilterDataController

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(filterDataController).build()
    }

    @Test
    fun `getBrandsForFilter returns list of brands`() {
        val brands = listOf(CardBrand(id = 1L, name = "Prizm"), CardBrand(id = 2L, name = "Select"))
        `when`(cardService.getAllBrands(1L)).thenReturn(brands)

        mockMvc.perform(get("/api/filters/brands").param("manufacturerId", "1"))
            .andExpect(status().isOk)
            .andExpect(header().exists("Cache-Control"))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].name").value("Prizm"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].name").value("Select"))
    }

    @Test
    fun `getThemesForFilter returns list of themes`() {
        val themes = listOf(CardTheme(id = 1L, name = "Base Set"), CardTheme(id = 2L, name = "Silver"))
        `when`(cardService.getAllThemes(1L, 2L)).thenReturn(themes)

        mockMvc.perform(get("/api/filters/themes").param("manufacturerId", "1").param("brandId", "2"))
            .andExpect(status().isOk)
            .andExpect(header().exists("Cache-Control"))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].name").value("Base Set"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].name").value("Silver"))
    }

    @Test
    fun `getVariantsForFilter returns list of variants`() {
        val variants = listOf(Variant(id = 1L, name = "Base"), Variant(id = 2L, name = "Gold /10"))
        `when`(cardService.getAllVariants(1L, 2L, 3L)).thenReturn(variants)

        mockMvc.perform(get("/api/filters/variants").param("manufacturerId", "1").param("brandId", "2").param("themeId", "3"))
            .andExpect(status().isOk)
            .andExpect(header().exists("Cache-Control"))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].name").value("Base"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].name").value("Gold /10"))
    }
}
