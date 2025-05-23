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
        val sportInstance = Sport(id = 1, name = "Basketball") // Real Sport instance for consistency
        val mockSportsList = listOf(sportInstance)

        val mockTeam = org.mockito.Mockito.mock(Team::class.java) // Explicit mock for Team
        `when`(mockTeam.id).thenReturn(1L)
        `when`(mockTeam.name).thenReturn("Michigan Wolverines") // Stub name for the team

        // explicitMockPlayer is used in mockCardsList, ensure its team and team.name are stubbed
        val explicitMockPlayer = org.mockito.Mockito.mock(Player::class.java)
        `when`(explicitMockPlayer.id).thenReturn(1L)
        `when`(explicitMockPlayer.name).thenReturn("Juwan")
        `when`(explicitMockPlayer.surname).thenReturn("Howard")
        `when`(explicitMockPlayer.sport).thenReturn(sportInstance)
        `when`(explicitMockPlayer.team).thenReturn(mockTeam) // ensure this mockTeam has .name stubbed

        val explicitMockTheme = org.mockito.Mockito.mock(CardTheme::class.java)
        `when`(explicitMockTheme.id).thenReturn(1L) // Stub id
        `when`(explicitMockTheme.name).thenReturn("Explicit Mock Theme") // Stub name

        val mockBrandForExplicitTheme = org.mockito.Mockito.mock(CardBrand::class.java)
        `when`(mockBrandForExplicitTheme.id).thenReturn(10L)
        `when`(mockBrandForExplicitTheme.name).thenReturn("Explicit Mock Brand")
        
        val mockManufacturerForExplicitBrand = org.mockito.Mockito.mock(CardManufacturer::class.java)
        `when`(mockManufacturerForExplicitBrand.id).thenReturn(100L)
        `when`(mockManufacturerForExplicitBrand.name).thenReturn("Explicit Mock Manufacturer")
        `when`(mockBrandForExplicitTheme.manufacturer).thenReturn(mockManufacturerForExplicitBrand)
        `when`(explicitMockTheme.brand).thenReturn(mockBrandForExplicitTheme)

        val explicitMockVariant = org.mockito.Mockito.mock(Variant::class.java)
        val mockCardsList = listOf(Card(id = 1, season = "2023", player = explicitMockPlayer, theme = explicitMockTheme, variant = explicitMockVariant, number = "1", printRun = 10, serialNumber = 1, rookieCard = false, gameUsedMaterial = false, autograph = false))
        
        val mockManufacturer = org.mockito.Mockito.mock(CardManufacturer::class.java)
        `when`(mockManufacturer.id).thenReturn(1L)
        `when`(mockManufacturer.name).thenReturn("Panini")
        // CardManufacturer does not have a 'sport' property
        val mockManufacturersList = listOf(mockManufacturer)
        
        // mockPlayer for the general players list in the model (if different from explicitMockPlayer)
        // If explicitMockPlayer is the ONLY player instance needed, this mockPlayer can be removed or merged.
        // For now, assuming it's for the general model attribute "players".
        val mockPlayerForList = org.mockito.Mockito.mock(Player::class.java)
        `when`(mockPlayerForList.id).thenReturn(1L)
        `when`(mockPlayerForList.name).thenReturn("Juwan")
        `when`(mockPlayerForList.surname).thenReturn("Howard")
        `when`(mockPlayerForList.sport).thenReturn(sportInstance)
        `when`(mockPlayerForList.team).thenReturn(mockTeam) // This team also needs its name stubbed.
        val mockPlayersList = listOf(mockPlayerForList)

        val mockBrand = org.mockito.Mockito.mock(CardBrand::class.java)
        `when`(mockBrand.id).thenReturn(1L)
        `when`(mockBrand.name).thenReturn("Prizm")
        `when`(mockBrand.manufacturer).thenReturn(mockManufacturer)
        val mockBrandsList = listOf(mockBrand)

        val mockTheme = org.mockito.Mockito.mock(CardTheme::class.java)
        `when`(mockTheme.id).thenReturn(1L)
        `when`(mockTheme.name).thenReturn("Base Set")
        `when`(mockTheme.brand).thenReturn(mockBrand)
        val mockThemesList = listOf(mockTheme)
        
        val mockSeasonsList = listOf("2023", "2024")

        // Stub the generic filter method for no filters
        `when`(cardService.getCardsFiltered(
            manufacturerId = org.mockito.ArgumentMatchers.isNull(),
            brandId = org.mockito.ArgumentMatchers.isNull(),
            themeId = org.mockito.ArgumentMatchers.isNull(),
            sportId = org.mockito.ArgumentMatchers.isNull(),
            playerId = org.mockito.ArgumentMatchers.isNull(),
            season = org.mockito.ArgumentMatchers.isNull(),
            gameUsed = org.mockito.ArgumentMatchers.isNull(),
            autograph = org.mockito.ArgumentMatchers.isNull(),
            variantId = org.mockito.ArgumentMatchers.isNull(),
            rookieCard = org.mockito.ArgumentMatchers.isNull(),
            printRunRangeKey = org.mockito.ArgumentMatchers.isNull(),
            teamId = org.mockito.ArgumentMatchers.isNull()
        )).thenReturn(mockCardsList)

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

        val sportInstanceForFilter = Sport(id = 2, name = "Football")
        val teamMockForFilter = org.mockito.Mockito.mock(Team::class.java)
        `when`(teamMockForFilter.id).thenReturn(2L)
        `when`(teamMockForFilter.name).thenReturn("Filter Test Team") // Stub name

        val playerMockForFilter = org.mockito.Mockito.mock(Player::class.java)
        `when`(playerMockForFilter.id).thenReturn(2L)
        `when`(playerMockForFilter.name).thenReturn("Filtered")
        `when`(playerMockForFilter.surname).thenReturn("Player")
        `when`(playerMockForFilter.sport).thenReturn(sportInstanceForFilter)
        `when`(playerMockForFilter.team).thenReturn(teamMockForFilter) // Assign stubbed team

        val themeMockForFilter = org.mockito.Mockito.mock(CardTheme::class.java)
        `when`(themeMockForFilter.id).thenReturn(2L) // Stub id
        `when`(themeMockForFilter.name).thenReturn("Filter Mock Theme") // Stub name

        val mockBrandForFilterTheme = org.mockito.Mockito.mock(CardBrand::class.java)
        `when`(mockBrandForFilterTheme.id).thenReturn(20L)
        `when`(mockBrandForFilterTheme.name).thenReturn("Filter Mock Brand")

        val mockManufacturerForFilterBrand = org.mockito.Mockito.mock(CardManufacturer::class.java)
        `when`(mockManufacturerForFilterBrand.id).thenReturn(200L)
        `when`(mockManufacturerForFilterBrand.name).thenReturn("Filter Mock Manufacturer")
        `when`(mockBrandForFilterTheme.manufacturer).thenReturn(mockManufacturerForFilterBrand)
        `when`(themeMockForFilter.brand).thenReturn(mockBrandForFilterTheme)

        val variantMockForFilter = org.mockito.Mockito.mock(Variant::class.java)
        val mockFilteredCards = listOf(Card(id = 2, season = testSeason, player = playerMockForFilter, theme = themeMockForFilter, variant = variantMockForFilter, number = "2", printRun = 10, serialNumber = 1, rookieCard = false, gameUsedMaterial = false, autograph = false))

        // Stub the generic filter method for season filter
        `when`(cardService.getCardsFiltered(
            manufacturerId = org.mockito.ArgumentMatchers.isNull(),
            brandId = org.mockito.ArgumentMatchers.isNull(),
            themeId = org.mockito.ArgumentMatchers.isNull(),
            sportId = org.mockito.ArgumentMatchers.isNull(),
            playerId = org.mockito.ArgumentMatchers.isNull(),
            season = org.mockito.ArgumentMatchers.eq(testSeason),
            gameUsed = org.mockito.ArgumentMatchers.isNull(),
            autograph = org.mockito.ArgumentMatchers.isNull(),
            variantId = org.mockito.ArgumentMatchers.isNull(),
            rookieCard = org.mockito.ArgumentMatchers.isNull(),
            printRunRangeKey = org.mockito.ArgumentMatchers.isNull(),
            teamId = org.mockito.ArgumentMatchers.isNull()
        )).thenReturn(mockFilteredCards)

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
        // Verify that the generic filter method was called with the correct season
        verify(cardService).getCardsFiltered(
            manufacturerId = org.mockito.ArgumentMatchers.isNull(),
            brandId = org.mockito.ArgumentMatchers.isNull(),
            themeId = org.mockito.ArgumentMatchers.isNull(),
            sportId = org.mockito.ArgumentMatchers.isNull(),
            playerId = org.mockito.ArgumentMatchers.isNull(),
            season = org.mockito.ArgumentMatchers.eq(testSeason),
            gameUsed = org.mockito.ArgumentMatchers.isNull(),
            autograph = org.mockito.ArgumentMatchers.isNull(),
            variantId = org.mockito.ArgumentMatchers.isNull(),
            rookieCard = org.mockito.ArgumentMatchers.isNull(),
            printRunRangeKey = org.mockito.ArgumentMatchers.isNull(),
            teamId = org.mockito.ArgumentMatchers.isNull()
        )
        // The following verifications might need to be removed or adjusted
        // if the controller solely relies on getCardsFiltered.
        // For now, let's keep them to see if getCardsBySeason was also called or not.
        // verify(cardService, org.mockito.Mockito.never()).getCardsByManufacturerId(anyLong())
        // verify(cardService, org.mockito.Mockito.never()).getCardsByBrandId(anyLong())
        // verify(cardService, org.mockito.Mockito.never()).getCardsByThemeId(anyLong())
        // verify(cardService, org.mockito.Mockito.never()).getCardsBySportId(anyLong())
        // verify(cardService, org.mockito.Mockito.never()).findAllByPlayerId(anyLong())
        // verify(cardService, org.mockito.Mockito.never()).getAllCards() 
    }
}
