package de.maulmann.cardcollection.controller

import de.maulmann.cardcollection.model.*
import de.maulmann.cardcollection.service.CardManufacturerService
import de.maulmann.cardcollection.service.CardService
import de.maulmann.cardcollection.service.PlayerService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get // Using Kotlin DSL for MockMvc

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

    @Test
    fun `testGetCards_defaultParams_returnsOkAndCardsView`() {
        // GIVEN
        val defaultPageable = PageRequest.of(0, 20, Sort.by("id"))
        val emptyPage = PageImpl<Card>(emptyList(), defaultPageable, 0)

        whenever(cardService.getCardsFiltered(
            anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), 
            anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), 
            eq(defaultPageable) // Match the specific Pageable object
        )).thenReturn(emptyPage)

        // Mock calls for dropdown population
        whenever(cardManufacturerService.getAllCardManufacturers()).thenReturn(emptyList())
        whenever(playerService.getPlayers()).thenReturn(emptyList())
        whenever(cardService.getAllBrands()).thenReturn(emptyList())
        whenever(cardService.getAllThemes()).thenReturn(emptyList())
        whenever(cardService.getAllSports()).thenReturn(emptyList())
        whenever(cardService.getAllSeasons()).thenReturn(emptyList())
        whenever(cardService.getAllVariants()).thenReturn(emptyList())
        whenever(cardService.getAllTeams()).thenReturn(emptyList())
        // Note: printRunRanges are hardcoded in Controller, no service call to mock for it.

        // WHEN & THEN
        mockMvc.get("/cards") {
            // Default params are typically page=0, size=20, sort=id,ASC from controller defaults
        }.andExpect {
            status { isOk() }
            view { name("cards") }
            model { attributeExists("cardPage", "currentPage", "totalPages", "totalItems", "pageSize") }
            model { attributeExists("manufacturers", "players", "brands", "themes", "sports", "seasons", "variants", "printRunRanges", "teams") }
        }

        verify(cardService).getCardsFiltered(
            eq(null), eq(null), eq(null), eq(null), eq(null), eq(null), eq(null), eq(null), eq(null), eq(null), eq(null), eq(null),
            eq(defaultPageable) // Verify with the default pageable defined in the controller
        )
    }

    @Test
    fun `testGetCards_withManufacturerIdParam_callsServiceWithParam`() {
        // GIVEN
        val manufacturerId = 1L
        val pageable = PageRequest.of(0, 20, Sort.by("id")) // Default pageable
        val emptyPage = PageImpl<Card>(emptyList(), pageable, 0)

        whenever(cardService.getCardsFiltered(
            eq(manufacturerId), // Expect manufacturerId to be 1L
            anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), 
            anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(),
            eq(pageable) // Match the specific Pageable object
        )).thenReturn(emptyPage)

        // Mock calls for dropdown population
        whenever(cardManufacturerService.getAllCardManufacturers()).thenReturn(emptyList())
        whenever(playerService.getPlayers()).thenReturn(emptyList())
        whenever(cardService.getAllBrands()).thenReturn(emptyList())
        whenever(cardService.getAllThemes()).thenReturn(emptyList())
        whenever(cardService.getAllSports()).thenReturn(emptyList())
        whenever(cardService.getAllSeasons()).thenReturn(emptyList())
        whenever(cardService.getAllVariants()).thenReturn(emptyList())
        whenever(cardService.getAllTeams()).thenReturn(emptyList())

        // WHEN & THEN
        mockMvc.get("/cards") {
            param("manufacturerId", manufacturerId.toString())
            // Default page, size, sort will be applied by controller
        }.andExpect {
            status { isOk() }
            view { name("cards") }
            model { attributeExists("cardPage", "currentPage", "totalPages", "totalItems", "pageSize") }
            model { attributeExists("manufacturers", "players", "brands", "themes", "sports", "seasons", "variants", "printRunRanges", "teams") }
        }

        verify(cardService).getCardsFiltered(
            eq(manufacturerId),
            eq(null), eq(null), eq(null), eq(null), eq(null), eq(null), eq(null), eq(null), eq(null), eq(null), eq(null),
            eq(pageable)
        )
    }

    // --- START Enhanced Mock Helper Functions ---
    private fun createMockSport(id: Long = 1L, name: String = "Basketball"): Sport = mock {
        on { this.id } doReturn id
        on { this.name } doReturn name
    }

    private fun createMockTeam(id: Long = 1L, name: String = "Test Team"): Team = mock {
        on { this.id } doReturn id
        on { this.name } doReturn name
    }

    private fun createMockPlayer(
        id: Long = 1L,
        name: String = "John",
        surname: String = "Doe",
        team: Team = createMockTeam(),
        sport: Sport = createMockSport()
    ): Player = mock {
        on { this.id } doReturn id
        on { this.name } doReturn name
        on { this.surname } doReturn surname
        on { this.team } doReturn team
        on { this.sport } doReturn sport
    }

    private fun createMockCardManufacturer(id: Long = 1L, name: String = "Panini"): CardManufacturer = mock {
        on { this.id } doReturn id
        on { this.name } doReturn name
    }

    private fun createMockCardBrand(
        id: Long = 1L,
        name: String = "Prizm",
        manufacturer: CardManufacturer = createMockCardManufacturer()
    ): CardBrand = mock {
        on { this.id } doReturn id
        on { this.name } doReturn name
        on { this.manufacturer } doReturn manufacturer
    }

    private fun createMockCardTheme(
        id: Long = 1L,
        name: String = "Base Set",
        brand: CardBrand = createMockCardBrand()
    ): CardTheme = mock {
        on { this.id } doReturn id
        on { this.name } doReturn name
        on { this.brand } doReturn brand
    }

    private fun createMockVariant(id: Long = 1L, name: String = "Silver"): Variant = mock {
        on { this.id } doReturn id
        on { this.name } doReturn name
    }

    // Updated createMockCard to include detailed nested mocks
    private fun createMockCard(
        id: Long,
        player: Player = createMockPlayer(),
        // theme parameter removed
        variant: Variant = createMockVariant(), // Variant itself might have a theme, handled by createMockVariant
        number: String = "101",
        season: String = "2023-24",
        printRun: Int = 100,
        serialNumber: Int = 10,
        rookieCard: Boolean = false,
        gameUsedMaterial: Boolean = false,
        autograph: Boolean = false
    ): Card = mock {
        on { this.id } doReturn id
        on { this.player } doReturn player
        // on { this.theme } doReturn theme // Line removed
        on { this.variant } doReturn variant
        on { this.number } doReturn number
        on { this.season } doReturn season
        on { this.printRun } doReturn printRun
        on { this.serialNumber } doReturn serialNumber
        on { this.rookieCard } doReturn rookieCard
        on { this.gameUsedMaterial } doReturn gameUsedMaterial
        on { this.autograph } doReturn autograph
    }
    // --- END Enhanced Mock Helper Functions ---
    
    private fun mockDropdownServices() {
        whenever(cardManufacturerService.getAllCardManufacturers()).thenReturn(emptyList())
        whenever(playerService.getPlayers()).thenReturn(emptyList())
        whenever(cardService.getAllBrands()).thenReturn(emptyList())
        whenever(cardService.getAllThemes()).thenReturn(emptyList())
        whenever(cardService.getAllSports()).thenReturn(emptyList())
        whenever(cardService.getAllSeasons()).thenReturn(emptyList())
        whenever(cardService.getAllVariants()).thenReturn(emptyList())
        whenever(cardService.getAllTeams()).thenReturn(emptyList())
    }

}
