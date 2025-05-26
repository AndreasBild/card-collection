package de.maulmann.cardcollection.controller

import com.fasterxml.jackson.databind.ObjectMapper
import de.maulmann.cardcollection.model.* // Card, Player, Theme, Variant, Sport, Team, CardBrand, CardManufacturer
import de.maulmann.cardcollection.service.CardManufacturerService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get // Using Kotlin DSL for MockMvc

@WebMvcTest(CardManufacturerController::class)
class CardManufacturerControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var cardManufacturerService: CardManufacturerService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    // Helper function to create a mock Player
    private fun createMockPlayer(): Player = mock {
        on { id } doReturn 1L
        on { name } doReturn "Test"
        on { surname } doReturn "Player"
        on { team } doReturn mock<Team>()
        on { sport } doReturn mock<Sport>()
    }
    
    // Helper function to create a mock Manufacturer (part of CardBrand)
    private fun createMockCardManufacturer(): CardManufacturer = mock {
        on { id } doReturn 1L
        on { name } doReturn "Panini"
    }

    // Helper function to create a mock CardBrand (part of CardTheme)
    private fun createMockCardBrand(): CardBrand = mock {
        on { id } doReturn 1L
        on { name } doReturn "Prizm"
        on { manufacturer } doReturn createMockCardManufacturer()
    }

    // Helper function to create a mock Theme
    private fun createMockTheme(): CardTheme = mock {
        on { id } doReturn 1L
        on { name } doReturn "Test Theme"
        on { brand } doReturn createMockCardBrand()
    }

    // Helper function to create a mock Variant
    private fun createMockVariant(): Variant = mock { // Corrected CardVariant to Variant
        on { id } doReturn 1L
        on { name } doReturn "Test Variant"
    }

    // Helper function to create a mock Card
    private fun createMockCard(id: Long, number: String = "123"): Card {
        return mock {
            on { this.id } doReturn id
            on { this.player } doReturn createMockPlayer()
            on { this.variant } doReturn createMockVariant() // Now calls the corrected createMockVariant
            on { this.number } doReturn number // Card number is a good property to check
            on { this.season } doReturn "2023-24"
            on { this.printRun } doReturn 100
            on { this.serialNumber } doReturn 10
            on { this.rookieCard } doReturn false
            on { this.gameUsedMaterial } doReturn false
            on { this.autograph } doReturn false
            // on { this.notes } doReturn "Mock card notes" // Removed as 'notes' is not a property of Card
        }
    }

    @Test
    fun `testGetCardsByManufacturerId_returnsCardsList`() {
        // GIVEN
        val manufacturerId = 1L
        val mockCards = listOf(createMockCard(1L, "CardA"), createMockCard(2L, "CardB"))
        whenever(cardManufacturerService.getCardsByManufacturerId(manufacturerId)).thenReturn(mockCards)

        // WHEN & THEN
        mockMvc.get("/api/cards/manufacturer/{manufacturerId}", manufacturerId)
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.length()") { value(mockCards.size) }
                jsonPath("$[0].id") { value(mockCards[0].id) }
                jsonPath("$[0].number") { value(mockCards[0].number) } // Checking 'number' property
                jsonPath("$[1].id") { value(mockCards[1].id) }
                jsonPath("$[1].number") { value(mockCards[1].number) }
            }
        verify(cardManufacturerService).getCardsByManufacturerId(manufacturerId)
    }

    @Test
    fun `testGetCardsByManufacturerId_serviceReturnsEmptyList_returnsEmptyList`() {
        // GIVEN
        val manufacturerId = 2L
        whenever(cardManufacturerService.getCardsByManufacturerId(manufacturerId)).thenReturn(emptyList())

        // WHEN & THEN
        mockMvc.get("/api/cards/manufacturer/{manufacturerId}", manufacturerId)
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.length()") { value(0) }
            }
        verify(cardManufacturerService).getCardsByManufacturerId(manufacturerId)
    }
}
