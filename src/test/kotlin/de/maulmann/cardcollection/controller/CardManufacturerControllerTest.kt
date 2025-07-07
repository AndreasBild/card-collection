package de.maulmann.cardcollection.controller

import com.fasterxml.jackson.databind.ObjectMapper
import de.maulmann.cardcollection.model.* // Card, Player, Theme, Variant, Sport, Team, CardBrand, CardManufacturer
import de.maulmann.cardcollection.service.CardManufacturerService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get // Using Kotlin DSL for MockMvc

@WebMvcTest(CardManufacturerController::class)
class CardManufacturerControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var cardManufacturerService: CardManufacturerService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    // Helper function to create a mock Player
    // Stable mock instances for dependencies
    private val stableMockTeam: Team = mock()
    private val stableMockSport: Sport = mock()
    private val stableMockPlayerInstance: Player = mock {
        on { id } doReturn 1L
        on { name } doReturn "Test"
        on { surname } doReturn "Player"
        on { team } doReturn stableMockTeam
        on { sport } doReturn stableMockSport
    }
    private val stableMockManufacturer: CardManufacturer = mock { on { id } doReturn 1L; on { name } doReturn "Panini" }
    private val stableMockBrand: CardBrand = mock { on { id } doReturn 1L; on { name } doReturn "Prizm"; on { manufacturer } doReturn stableMockManufacturer }
    private val stableMockTheme: CardTheme = mock { on { id } doReturn 1L; on { name } doReturn "Test Theme"; on { brand } doReturn stableMockBrand }
    private val stableMockVariant: Variant = mock { on { id } doReturn 1L; on { name } doReturn "Test Variant"; on {theme} doReturn stableMockTheme }
    private val stableMockSeason: Season = mock { on { id } doReturn 1L; on { name } doReturn "2023-24" }
    
    private fun getStableMockPlayer(): Player = stableMockPlayerInstance
    private fun getStableMockTheme(): CardTheme = stableMockTheme
    private fun getStableMockVariant(): Variant = stableMockVariant
    private fun getStableMockSeason(): Season = stableMockSeason


    // Helper function to create a mock Card
    private fun createMockCard(id: Long, number: String = "123"): Card {
        return mock {
            on { this.id } doReturn id
            on { player } doReturn getStableMockPlayer()
            on { variant } doReturn getStableMockVariant()
            on { this.number } doReturn number 
            on { season } doReturn getStableMockSeason()
            on { theme } doReturn getStableMockTheme() // Added theme directly to card mock
            on { printRun } doReturn 100
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
