package de.maulmann.cardcollection.controller

import de.maulmann.cardcollection.model.*
import de.maulmann.cardcollection.service.CardManufacturerService
import de.maulmann.cardcollection.service.CardService
import de.maulmann.cardcollection.service.PlayerService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(CardController::class)
class CardControllerTests {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var cardService: CardService

    @MockBean
    private lateinit var cardManufacturerService: CardManufacturerService

    @MockBean
    private lateinit var playerService: PlayerService

    private lateinit var sampleCards: List<Card>

    // Helper entities
    private val sport1 = Sport(1, "Basketball")
    private val team1 = Team(1, "Chicago Bulls")
    private val player1 = Player(1, "Michael", "Jordan", team1, sport1)
    private val manufacturer1 = CardManufacturer(1, "Upper Deck")
    private val brand1 = CardBrand(1, "Exquisite Collection", manufacturer1)
    private val theme1 = CardTheme(1, "Base Set", brand1)
    private val variant1 = Variant(1, "Gold")


    @BeforeEach
    fun setUp() {
        sampleCards = listOf(
            Card(1, 100, 10, "1997-98", "1", rookieCard = false, gameUsedMaterial = true, autograph = true, player1, theme1, variant1)
            // Add more sample cards if needed for specific controller tests, though service is mocked
        )

        // Mock service methods that are called in the controller's getCards method for populating filters
        `when`(cardManufacturerService.getAllCardManufacturers()).thenReturn(emptyList())
        `when`(playerService.getPlayers()).thenReturn(emptyList())
        `when`(cardService.getAllBrands()).thenReturn(emptyList())
        `when`(cardService.getAllThemes()).thenReturn(emptyList())
        `when`(cardService.getAllSports()).thenReturn(emptyList())
        `when`(cardService.getAllSeasons()).thenReturn(emptyList())
        `when`(cardService.getAllVariants()).thenReturn(emptyList()) // Mock for getAllVariants
    }

    @Test
    fun `getCards when no parameters should call getCardsFiltered with nulls and return cards view`() {
        `when`(cardService.getCardsFiltered(null, null, null, null, null, null, null, null, null, null))
            .thenReturn(sampleCards)

        mockMvc.perform(get("/cards"))
            .andExpect(status().isOk)
            .andExpect(view().name("cards"))
            .andExpect(model().attributeExists("cards"))
            .andExpect(model().attribute("cards", sampleCards))
            .andExpect(model().attributeExists("manufacturers"))
            .andExpect(model().attributeExists("players"))
            .andExpect(model().attributeExists("brands"))
            .andExpect(model().attributeExists("themes"))
            .andExpect(model().attributeExists("sports"))
            .andExpect(model().attributeExists("seasons"))
            .andExpect(model().attributeExists("variants")) // Check for variants

        verify(cardService).getCardsFiltered(null, null, null, null, null, null, null, null, null, null)
    }

    @Test
    fun `getCards with gameUsed true should call getCardsFiltered with gameUsed true`() {
        val gameUsed = true
        `when`(cardService.getCardsFiltered(null, null, null, null, null, null, gameUsed, null, null, null))
            .thenReturn(sampleCards.filter { it.gameUsedMaterial == gameUsed })

        mockMvc.perform(get("/cards").param("gameUsed", gameUsed.toString()))
            .andExpect(status().isOk)
            .andExpect(view().name("cards"))
            .andExpect(model().attributeExists("cards"))
            .andExpect(model().attributeExists("variants"))

        verify(cardService).getCardsFiltered(null, null, null, null, null, null, gameUsed, null, null, null)
    }

    @Test
    fun `getCards with autograph false should call getCardsFiltered with autograph false`() {
        val autograph = false
        `when`(cardService.getCardsFiltered(null, null, null, null, null, null, null, autograph, null, null))
            .thenReturn(sampleCards.filter { it.autograph == autograph })

        mockMvc.perform(get("/cards").param("autograph", autograph.toString()))
            .andExpect(status().isOk)
            .andExpect(view().name("cards"))
            .andExpect(model().attributeExists("cards"))
            .andExpect(model().attributeExists("variants"))

        verify(cardService).getCardsFiltered(null, null, null, null, null, null, null, autograph, null, null)
    }

    @Test
    fun `getCards with manufacturerId and gameUsed true and autograph false`() {
        val manufacturerId = 1L
        val gameUsed = true
        val autograph = false
        `when`(cardService.getCardsFiltered(manufacturerId, null, null, null, null, null, gameUsed, autograph, null, null))
            .thenReturn(sampleCards) // Actual filtering is tested in service layer

        mockMvc.perform(get("/cards")
            .param("manufacturerId", manufacturerId.toString())
            .param("gameUsed", gameUsed.toString())
            .param("autograph", autograph.toString()))
            .andExpect(status().isOk)
            .andExpect(view().name("cards"))
            .andExpect(model().attributeExists("cards"))
            .andExpect(model().attributeExists("variants"))

        verify(cardService).getCardsFiltered(manufacturerId, null, null, null, null, null, gameUsed, autograph, null, null)
    }

    @Test
    fun `getCards with season and playerId`() {
        val season = "1997-98"
        val playerId = 1L
        `when`(cardService.getCardsFiltered(null, null, null, null, playerId, season, null, null, null, null))
            .thenReturn(sampleCards)

        mockMvc.perform(get("/cards")
            .param("season", season)
            .param("playerId", playerId.toString()))
            .andExpect(status().isOk)
            .andExpect(view().name("cards"))
            .andExpect(model().attributeExists("cards"))
            .andExpect(model().attributeExists("variants"))

        verify(cardService).getCardsFiltered(null, null, null, null, playerId, season, null, null, null, null)
    }
    
    @Test
    fun `getCards with all parameters set excluding new ones`() {
        val manufacturerId = 1L
        val brandId = 1L
        val themeId = 1L
        val sportId = 1L
        val playerId = 1L
        val season = "1997-98"
        val gameUsed = true
        val autograph = false

        `when`(cardService.getCardsFiltered(manufacturerId, brandId, themeId, sportId, playerId, season, gameUsed, autograph, null, null))
            .thenReturn(sampleCards)

        mockMvc.perform(get("/cards")
            .param("manufacturerId", manufacturerId.toString())
            .param("brandId", brandId.toString())
            .param("themeId", themeId.toString())
            .param("sportId", sportId.toString())
            .param("playerId", playerId.toString())
            .param("season", season)
            .param("gameUsed", gameUsed.toString())
            .param("autograph", autograph.toString()))
            .andExpect(status().isOk)
            .andExpect(view().name("cards"))
            .andExpect(model().attributeExists("cards"))
            .andExpect(model().attributeExists("variants"))

        verify(cardService).getCardsFiltered(manufacturerId, brandId, themeId, sportId, playerId, season, gameUsed, autograph, null, null)
    }

    @Test
    fun `getCards with variantId should call getCardsFiltered with variantId`() {
        val variantId = 123L
        `when`(cardService.getCardsFiltered(null, null, null, null, null, null, null, null, variantId, null))
            .thenReturn(sampleCards)

        mockMvc.perform(get("/cards").param("variantId", variantId.toString()))
            .andExpect(status().isOk)
            .andExpect(view().name("cards"))
            .andExpect(model().attributeExists("cards"))
            .andExpect(model().attributeExists("variants"))

        verify(cardService).getCardsFiltered(null, null, null, null, null, null, null, null, variantId, null)
    }

    @Test
    fun `getCards with rookieCard true should call getCardsFiltered with rookieCard true`() {
        val rookieCard = true
        `when`(cardService.getCardsFiltered(null, null, null, null, null, null, null, null, null, rookieCard))
            .thenReturn(sampleCards)

        mockMvc.perform(get("/cards").param("rookieCard", rookieCard.toString()))
            .andExpect(status().isOk)
            .andExpect(view().name("cards"))
            .andExpect(model().attributeExists("cards"))
            .andExpect(model().attributeExists("variants"))

        verify(cardService).getCardsFiltered(null, null, null, null, null, null, null, null, null, rookieCard)
    }

    @Test
    fun `getCards with rookieCard false should call getCardsFiltered with rookieCard false`() {
        val rookieCard = false
        `when`(cardService.getCardsFiltered(null, null, null, null, null, null, null, null, null, rookieCard))
            .thenReturn(sampleCards)

        mockMvc.perform(get("/cards").param("rookieCard", rookieCard.toString()))
            .andExpect(status().isOk)
            .andExpect(view().name("cards"))
            .andExpect(model().attributeExists("cards"))
            .andExpect(model().attributeExists("variants"))

        verify(cardService).getCardsFiltered(null, null, null, null, null, null, null, null, null, rookieCard)
    }

    @Test
    fun `getCards with variantId and rookieCard true should call getCardsFiltered with both`() {
        val variantId = 456L
        val rookieCard = true
        `when`(cardService.getCardsFiltered(null, null, null, null, null, null, null, null, variantId, rookieCard))
            .thenReturn(sampleCards)

        mockMvc.perform(get("/cards")
            .param("variantId", variantId.toString())
            .param("rookieCard", rookieCard.toString()))
            .andExpect(status().isOk)
            .andExpect(view().name("cards"))
            .andExpect(model().attributeExists("cards"))
            .andExpect(model().attributeExists("variants"))

        verify(cardService).getCardsFiltered(null, null, null, null, null, null, null, null, variantId, rookieCard)
    }
    
    @Test
    fun `getCards with all parameters set including new ones`() {
        val manufacturerId = 1L
        val brandId = 2L
        val themeId = 3L
        val sportId = 4L
        val playerId = 5L
        val season = "2022-23"
        val gameUsed = false
        val autograph = true
        val variantId = 789L
        val rookieCard = false

        `when`(cardService.getCardsFiltered(manufacturerId, brandId, themeId, sportId, playerId, season, gameUsed, autograph, variantId, rookieCard))
            .thenReturn(sampleCards)

        mockMvc.perform(get("/cards")
            .param("manufacturerId", manufacturerId.toString())
            .param("brandId", brandId.toString())
            .param("themeId", themeId.toString())
            .param("sportId", sportId.toString())
            .param("playerId", playerId.toString())
            .param("season", season)
            .param("gameUsed", gameUsed.toString())
            .param("autograph", autograph.toString())
            .param("variantId", variantId.toString())
            .param("rookieCard", rookieCard.toString()))
            .andExpect(status().isOk)
            .andExpect(view().name("cards"))
            .andExpect(model().attributeExists("cards"))
            .andExpect(model().attributeExists("variants"))

        verify(cardService).getCardsFiltered(manufacturerId, brandId, themeId, sportId, playerId, season, gameUsed, autograph, variantId, rookieCard)
    }
}
