package de.maulmann.cardcollection.controller

import de.maulmann.cardcollection.model.*
import de.maulmann.cardcollection.service.CardManufacturerService
import de.maulmann.cardcollection.service.CardService
import de.maulmann.cardcollection.service.PlayerService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.mockito.ArgumentMatchers.anyString
import org.mockito.ArgumentMatchers.anyLong

@WebMvcTest(CardController::class)
class CardControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var cardService: CardService

    @MockBean
    private lateinit var cardManufacturerService: CardManufacturerService

    @MockBean
    private lateinit var playerService: PlayerService

    // Helper for mock creation, similar to service test
    private inline fun <reified T> mock(): T = org.mockito.Mockito.mock(T::class.java)

    @Test
    fun `getCards_noFilters_shouldReturnCardsViewWithAllData`() {
        // Given
        val mockCardsList = listOf(Card(id = 1, season = "2023", player = mock(), theme = mock(), variant = mock(), number = "1", printRun = 10, serialNumber = 1, rookieCard = false, gameUsedMaterial = false, autograph = false))
        val mockManufacturersList = listOf(CardManufacturer(1, "Panini", "USA"))
        val mockPlayersList = listOf(Player(1, "Juwan", "Howard", mock()))
        val mockBrandsList = listOf(CardBrand(1, "Prizm", mockManufacturersList[0]))
        val mockThemesList = listOf(CardTheme(1, "Base Set", mockBrandsList[0]))
        val mockSportsList = listOf(Sport(1, "Basketball"))
        val mockSeasonsList = listOf("2023", "2024")

        `when`(cardService.getAllCards()).thenReturn(mockCardsList)
        `when`(cardManufacturerService.getAllCardManufacturers()).thenReturn(mockManufacturersList)
        `when`(playerService.getPlayers()).thenReturn(mockPlayersList)
        `when`(cardService.getAllBrands()).thenReturn(mockBrandsList)
        `when`(cardService.getAllThemes()).thenReturn(mockThemesList)
        `when`(cardService.getAllSports()).thenReturn(mockSportsList)
        `when`(cardService.getAllSeasons()).thenReturn(mockSeasonsList)

        // When & Then
        mockMvc.perform(get("/cards"))
            .andExpect(status().isOk)
            .andExpect(view().name("cards"))
            .andExpect(model().attribute("cards", mockCardsList))
            .andExpect(model().attribute("manufacturers", mockManufacturersList))
            .andExpect(model().attribute("players", mockPlayersList))
            .andExpect(model().attribute("brands", mockBrandsList))
            .andExpect(model().attribute("themes", mockThemesList))
            .andExpect(model().attribute("sports", mockSportsList))
            .andExpect(model().attribute("seasons", mockSeasonsList))
    }

    @Test
    fun `getCards_withSeasonFilter_shouldReturnFilteredCards`() {
        // Given
        val testSeason = "TestSeason"
        val mockFilteredCards = listOf(Card(id = 2, season = testSeason, player = mock(), theme = mock(), variant = mock(), number = "2", printRun = 10, serialNumber = 1, rookieCard = false, gameUsedMaterial = false, autograph = false))

        // Mock the specific service call for the season filter
        `when`(cardService.getCardsBySeason(testSeason)).thenReturn(mockFilteredCards)

        // Mock other service calls for dropdowns (can return empty lists or sample data)
        `when`(cardManufacturerService.getAllCardManufacturers()).thenReturn(emptyList())
        `when`(playerService.getPlayers()).thenReturn(emptyList())
        `when`(cardService.getAllBrands()).thenReturn(emptyList())
        `when`(cardService.getAllThemes()).thenReturn(emptyList())
        `when`(cardService.getAllSports()).thenReturn(emptyList())
        `when`(cardService.getAllSeasons()).thenReturn(listOf(testSeason, "OtherSeason")) // include the test season

        // When & Then
        mockMvc.perform(get("/cards").param("season", testSeason))
            .andExpect(status().isOk)
            .andExpect(view().name("cards"))
            .andExpect(model().attribute("cards", mockFilteredCards)) // Check for filtered cards
            .andExpect(model().attributeExists("manufacturers", "players", "brands", "themes", "sports", "seasons")) // Ensure dropdown data is still present

        // Verify that the correct service method was called for filtering
        verify(cardService).getCardsBySeason(testSeason)
        // Verify that other filter methods were NOT called (demonstrating "first filter wins")
        verify(cardService, org.mockito.Mockito.never()).getCardsByManufacturerId(anyLong())
        verify(cardService, org.mockito.Mockito.never()).getCardsByBrandId(anyLong())
        verify(cardService, org.mockito.Mockito.never()).getCardsByThemeId(anyLong())
        verify(cardService, org.mockito.Mockito.never()).getCardsBySportId(anyLong())
        verify(cardService, org.mockito.Mockito.never()).findAllByPlayerId(anyLong())
        verify(cardService, org.mockito.Mockito.never()).getAllCards() // Not called because season filter was applied
    }
}
