package de.maulmann.cardcollection.controller

import de.maulmann.cardcollection.model.CardBrand
import de.maulmann.cardcollection.model.CardTheme
import de.maulmann.cardcollection.model.CardManufacturer // Added for mock()
import org.mockito.Mockito.mock // Added for mock()

import de.maulmann.cardcollection.service.CardService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.mockito.Mock
import org.springframework.context.annotation.Import
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(FilterDataController::class)
@Import(CardService::class)
class FilterDataControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Mock
    private lateinit var cardService: CardService

    @Test
    fun `getBrandsForFilter should return brands from service`() {
        val manufacturerId = 1L
        // Using a properly typed mock for CardManufacturer
        val mockManufacturer = mock(CardManufacturer::class.java)
        val mockBrands = listOf(CardBrand(id = 1L, name = "Prizm", manufacturer = mockManufacturer))
        `when`(cardService.getAllBrands(manufacturerId)).thenReturn(mockBrands)

        mockMvc.perform(get("/api/filters/brands").param("manufacturerId", manufacturerId.toString()))
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$[0].id").value(1L))
            .andExpect(jsonPath("$[0].name").value("Prizm"))

        verify(cardService).getAllBrands(manufacturerId)
    }

    @Test
    fun `getBrandsForFilter should call service with null if no manufacturerId`() {
        val mockManufacturer = mock(CardManufacturer::class.java)
        val mockBrands = listOf(CardBrand(id = 2L, name = "Select", manufacturer = mockManufacturer))
        `when`(cardService.getAllBrands(null)).thenReturn(mockBrands)

        mockMvc.perform(get("/api/filters/brands"))
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$[0].id").value(2L))
            .andExpect(jsonPath("$[0].name").value("Select"))
            
        verify(cardService).getAllBrands(null)
    }

    @Test
    fun `getThemesForFilter should return themes from service with brandId`() {
        val brandId = 1L
        val mockBrand = mock(CardBrand::class.java) // Properly typed mock
        val mockThemes = listOf(CardTheme(id = 1L, name = "Base Set", brand = mockBrand)) 
        `when`(cardService.getAllThemes(manufacturerId = null, brandId = brandId)).thenReturn(mockThemes)

        mockMvc.perform(get("/api/filters/themes").param("brandId", brandId.toString()))
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$[0].id").value(1L))
            .andExpect(jsonPath("$[0].name").value("Base Set"))

        verify(cardService).getAllThemes(manufacturerId = null, brandId = brandId)
    }
    
    @Test
    fun `getThemesForFilter should return themes from service with manufacturerId`() {
        val manufacturerId = 1L
        val mockBrand = mock(CardBrand::class.java)
        val mockThemes = listOf(CardTheme(id = 2L, name = "Inserts", brand = mockBrand))
        `when`(cardService.getAllThemes(manufacturerId = manufacturerId, brandId = null)).thenReturn(mockThemes)

        mockMvc.perform(get("/api/filters/themes").param("manufacturerId", manufacturerId.toString()))
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$[0].id").value(2L))
            .andExpect(jsonPath("$[0].name").value("Inserts"))

        verify(cardService).getAllThemes(manufacturerId = manufacturerId, brandId = null)
    }

    @Test
    fun `getThemesForFilter should call service with nulls if no params`() {
        val mockBrand = mock(CardBrand::class.java)
        val mockThemes = listOf(CardTheme(id = 3L, name = "All Themes", brand = mockBrand))
        `when`(cardService.getAllThemes(manufacturerId = null, brandId = null)).thenReturn(mockThemes)

        mockMvc.perform(get("/api/filters/themes"))
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$[0].id").value(3L))
            .andExpect(jsonPath("$[0].name").value("All Themes"))

        verify(cardService).getAllThemes(manufacturerId = null, brandId = null)
    }
     @Test
    fun `getThemesForFilter should prioritize brandId over manufacturerId`() {
        val brandId = 1L
        val manufacturerId = 2L // Different from what brandId might imply, to test precedence
        val mockBrand = mock(CardBrand::class.java)
        val mockThemes = listOf(CardTheme(id = 4L, name = "Brand Specific Theme", brand = mockBrand))
        `when`(cardService.getAllThemes(manufacturerId = manufacturerId, brandId = brandId)).thenReturn(mockThemes)

        mockMvc.perform(get("/api/filters/themes")
                .param("brandId", brandId.toString())
                .param("manufacturerId", manufacturerId.toString()))
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$[0].id").value(4L))
            .andExpect(jsonPath("$[0].name").value("Brand Specific Theme"))

        // Service logic: if brandId is present, it's used. manufacturerId is then for context if brandId is NOT present.
        // So, the service should be called with both, but its internal logic handles precedence.
        verify(cardService).getAllThemes(manufacturerId = manufacturerId, brandId = brandId)
    }
}
