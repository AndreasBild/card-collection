package de.maulmann.cardcollection.controller

import de.maulmann.cardcollection.model.*
import de.maulmann.cardcollection.service.CardService
import de.maulmann.cardcollection.service.PlayerService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.servlet.view.InternalResourceViewResolver

@ExtendWith(MockitoExtension::class)
class CardControllerTest {

    private lateinit var mockMvc: MockMvc

    @Mock
    private lateinit var cardService: CardService

    @Mock
    private lateinit var playerService: PlayerService

    @InjectMocks
    private lateinit var cardController: CardController

    @BeforeEach
    fun setUp() {
        val viewResolver = InternalResourceViewResolver().apply {
            setPrefix("/templates/")
            setSuffix(".html")
        }
        mockMvc = MockMvcBuilders.standaloneSetup(cardController)
            .setViewResolvers(viewResolver)
            .build()
    }

    @Test
    fun `getCards renders cards view with pagination and model attributes`() {
        val manufacturer = CardManufacturer(id = 1L, name = "Panini")
        val brand = CardBrand(id = 1L, name = "Prizm")
        val theme = CardTheme(id = 1L, name = "Base")
        val season = Season(id = 1L, name = "2023-24")
        val variant = Variant(id = 1L, name = "Base")
        val sport = Sport(id = 1L, name = "Basketball")
        val player = Player(id = 1L, name = "Juwan", surname = "Howard", sport = sport)
        val team = Team(id = 1L, name = "Miami Heat")

        val card = Card(
            id = 1L,
            printRun = 100,
            serialNumber = 1,
            season = season,
            number = "10",
            rookieCard = true,
            gameUsedMaterial = false,
            autograph = false,
            manufacturer = manufacturer,
            brand = brand,
            variant = variant,
            theme = theme
        )
        card.cardPlayers.add(CardPlayer(id = CardPlayerId(1L, 1L), card = card, player = player, team = team))

        val page = PageImpl(listOf(card), PageRequest.of(0, 20), 1)

        whenever(cardService.getCardsFiltered(any(), any())).thenReturn(page)
        whenever(cardService.getAllCardManufacturers()).thenReturn(listOf(manufacturer))
        whenever(playerService.getPlayers()).thenReturn(listOf(player))
        whenever(cardService.getAllBrands()).thenReturn(listOf(brand))
        whenever(cardService.getAllThemes()).thenReturn(listOf(theme))
        whenever(cardService.getAllSports()).thenReturn(listOf(sport))
        whenever(cardService.getAllSeasons()).thenReturn(listOf(season))
        whenever(cardService.getAllVariants()).thenReturn(listOf(variant))
        whenever(cardService.getAllTeams()).thenReturn(listOf(team))

        mockMvc.perform(get("/cards"))
            .andExpect(status().isOk)
            .andExpect(view().name("cards"))
            .andExpect(model().attributeExists("cards"))
            .andExpect(model().attributeExists("cardPage"))
            .andExpect(model().attributeExists("currentPage"))
            .andExpect(model().attributeExists("totalPages"))
            .andExpect(model().attributeExists("totalItems"))
            .andExpect(model().attribute("totalItems", 1L))
            .andExpect(model().attributeExists("jsonLdSchema"))
    }

    @Test
    fun `getCards handles root path slash`() {
        val page = PageImpl(emptyList<Card>(), PageRequest.of(0, 20), 0)
        whenever(cardService.getCardsFiltered(any(), any())).thenReturn(page)

        mockMvc.perform(get("/"))
            .andExpect(status().isOk)
            .andExpect(view().name("cards"))
            .andExpect(model().attributeExists("jsonLdSchema"))
    }

    @Test
    fun `getCards with sorting and filtering parameters`() {
        val page = PageImpl(emptyList<Card>(), PageRequest.of(0, 10), 0)
        whenever(cardService.getCardsFiltered(any(), any())).thenReturn(page)

        mockMvc.perform(
            get("/cards")
                .param("page", "0")
                .param("size", "10")
                .param("sort", "number,desc")
                .param("rookieCard", "true")
        )
            .andExpect(status().isOk)
            .andExpect(view().name("cards"))
            .andExpect(model().attribute("currentSortProperty", "number"))
            .andExpect(model().attribute("currentSortDirection", "DESC"))
    }

    @Test
    fun `getCards with size all parameter`() {
        val page = PageImpl(emptyList<Card>(), PageRequest.of(0, 100_000), 0)
        whenever(cardService.getCardsFiltered(any(), any())).thenReturn(page)

        mockMvc.perform(
            get("/cards")
                .param("size", "all")
        )
            .andExpect(status().isOk)
            .andExpect(view().name("cards"))
            .andExpect(model().attribute("pageSize", "all"))
            .andExpect(model().attribute("isAllSize", true))
    }
}
