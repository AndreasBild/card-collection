package de.maulmann.cardcollection.controller

import com.fasterxml.jackson.databind.ObjectMapper
import de.maulmann.cardcollection.model.CardBrand
import de.maulmann.cardcollection.model.CardManufacturer
import de.maulmann.cardcollection.model.CardTheme
import de.maulmann.cardcollection.service.CardService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get // Using Kotlin DSL for MockMvc

@WebMvcTest(FilterDataController::class)
class FilterDataControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var cardService: CardService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private fun createMockManufacturer(id: Long, name: String): CardManufacturer = mock {
        on { this.id } doReturn id
        on { this.name } doReturn name
    }

    private fun createMockBrand(id: Long, name: String, manufacturerId: Long = 1L): CardBrand {
        val manufacturerMock = createMockManufacturer(manufacturerId, "Manuf ${manufacturerId}")
        return mock {
            on { this.id } doReturn id
            on { this.name } doReturn name
            on { this.manufacturer } doReturn manufacturerMock
        }
    }

    private fun createMockTheme(id: Long, name: String, brandId: Long = 1L): CardTheme {
        val brandMock = createMockBrand(brandId, "Brand ${brandId}")
        return mock {
            on { this.id } doReturn id
            on { this.name } doReturn name
            on { this.brand } doReturn brandMock
        }
    }

    @Test
    fun `testGetBrandsForFilter_noManufacturerId_returnsBrandsList`() {
        // GIVEN
        val mockBrands = listOf(createMockBrand(1L, "Brand A"), createMockBrand(2L, "Brand B"))
        whenever(cardService.getAllBrands(null)).thenReturn(mockBrands)

        // WHEN & THEN
        mockMvc.get("/api/filters/brands")
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.length()") { value(mockBrands.size) }
                jsonPath("$[0].id") { value(mockBrands[0].id) }
                jsonPath("$[0].name") { value(mockBrands[0].name) }
                jsonPath("$[1].id") { value(mockBrands[1].id) }
                jsonPath("$[1].name") { value(mockBrands[1].name) }
            }
        verify(cardService).getAllBrands(null)
    }

    @Test
    fun `testGetBrandsForFilter_withManufacturerId_returnsFilteredBrandsList`() {
        // GIVEN
        val manufacturerId = 1L
        val mockBrands = listOf(createMockBrand(3L, "Brand C", manufacturerId))
        whenever(cardService.getAllBrands(manufacturerId)).thenReturn(mockBrands)

        // WHEN & THEN
        mockMvc.get("/api/filters/brands?manufacturerId=$manufacturerId")
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.length()") { value(mockBrands.size) }
                jsonPath("$[0].id") { value(mockBrands[0].id) }
                jsonPath("$[0].name") { value(mockBrands[0].name) }
            }
        verify(cardService).getAllBrands(manufacturerId)
    }

    @Test
    fun `testGetThemesForFilter_noIds_returnsThemesList`() {
        // GIVEN
        val mockThemes = listOf(createMockTheme(1L, "Theme X"), createMockTheme(2L, "Theme Y"))
        whenever(cardService.getAllThemes(null, null)).thenReturn(mockThemes)

        // WHEN & THEN
        mockMvc.get("/api/filters/themes")
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.length()") { value(mockThemes.size) }
                jsonPath("$[0].id") { value(mockThemes[0].id) }
                jsonPath("$[0].name") { value(mockThemes[0].name) }
            }
        verify(cardService).getAllThemes(null, null)
    }

    @Test
    fun `testGetThemesForFilter_withManufacturerId_returnsFilteredThemesList`() {
        // GIVEN
        val manufacturerId = 1L
        val mockThemes = listOf(createMockTheme(3L, "Theme Z", brandId = 10L)) // Assuming brand has this manufacturer
        whenever(cardService.getAllThemes(manufacturerId = manufacturerId, brandId = null)).thenReturn(mockThemes)

        // WHEN & THEN
        mockMvc.get("/api/filters/themes?manufacturerId=$manufacturerId")
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.length()") { value(mockThemes.size) }
                jsonPath("$[0].id") { value(mockThemes[0].id) }
                jsonPath("$[0].name") { value(mockThemes[0].name) }
            }
        verify(cardService).getAllThemes(manufacturerId = manufacturerId, brandId = null)
    }

    @Test
    fun `testGetThemesForFilter_withBrandId_returnsFilteredThemesList`() {
        // GIVEN
        val brandId = 1L
        val mockThemes = listOf(createMockTheme(4L, "Theme Alpha", brandId = brandId))
        whenever(cardService.getAllThemes(manufacturerId = null, brandId = brandId)).thenReturn(mockThemes)

        // WHEN & THEN
        mockMvc.get("/api/filters/themes?brandId=$brandId")
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.length()") { value(mockThemes.size) }
                jsonPath("$[0].id") { value(mockThemes[0].id) }
                jsonPath("$[0].name") { value(mockThemes[0].name) }
            }
        verify(cardService).getAllThemes(manufacturerId = null, brandId = brandId)
    }

    @Test
    fun `testGetThemesForFilter_withManufacturerAndBrandId_returnsFilteredThemesList`() {
        // GIVEN
        val manufacturerId = 1L
        val brandId = 2L
        val mockThemes = listOf(createMockTheme(5L, "Theme Beta", brandId = brandId))
        // Assuming brandId=2L is associated with manufacturerId=1L for this test to be meaningful
        whenever(cardService.getAllThemes(manufacturerId = manufacturerId, brandId = brandId)).thenReturn(mockThemes)

        // WHEN & THEN
        mockMvc.get("/api/filters/themes?manufacturerId=$manufacturerId&brandId=$brandId")
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.length()") { value(mockThemes.size) }
                jsonPath("$[0].id") { value(mockThemes[0].id) }
                jsonPath("$[0].name") { value(mockThemes[0].name) }
            }
        verify(cardService).getAllThemes(manufacturerId = manufacturerId, brandId = brandId)
    }
}
