package de.maulmann.cardcollection.controller

import de.maulmann.cardcollection.model.*
import de.maulmann.cardcollection.service.CardManufacturerService
import de.maulmann.cardcollection.service.CardService
import de.maulmann.cardcollection.service.PlayerService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@AutoConfigureMockMvc
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
    fun `getCards should return cards view with default pagination and sorting`() {
        val sport = Sport(name = "Basketball")
        val team = Team(name = "Lakers")
        val player = Player(name = "LeBron", surname = "James", sport = sport, team = team)
        val season = Season(name = "2022-23")
        val variant = Variant(name = "Silver Prizm")
        val manufacturer = CardManufacturer(name = "Panini")
        val brand = CardBrand(name = "Prizm", manufacturer = manufacturer)
        val theme = CardTheme(name = "Base Set", brand = brand)
        val cards = listOf(
            Card(number = "23", autograph = false, gameUsedMaterial = false, rookieCard = false, printRun = 100, serialNumber = 1, player = player, season = season, variant = variant, theme = theme)
        )
        val cardPage = PageImpl(cards)
        val pageable = PageRequest.of(0, 20)

        whenever(cardService.getCardsFiltered(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), any()))
            .thenReturn(cardPage)

        mockMvc.perform(get("/cards"))
            .andExpect(status().isOk)
            .andExpect(view().name("cards"))
            .andExpect(model().attributeExists("cardPage"))
            .andExpect(model().attribute("currentPage", 0))
            .andExpect(model().attribute("totalPages", 1))
    }

    @Test
    fun `getCards with specific page and size should be handled correctly`() {
        val cards = emptyList<Card>()
        val cardPage = PageImpl(cards)
        val pageable = PageRequest.of(1, 10)


        whenever(cardService.getCardsFiltered(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), any()))
            .thenReturn(cardPage)

        mockMvc.perform(get("/cards?page=1&size=10"))
            .andExpect(status().isOk)
            .andExpect(view().name("cards"))
    }

    @Test
    fun `getCards with sorting parameters should be handled correctly`() {
        val cards = emptyList<Card>()
        val cardPage = PageImpl(cards)
        val pageable = PageRequest.of(0, 20)

        whenever(cardService.getCardsFiltered(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), any()))
            .thenReturn(cardPage)

        mockMvc.perform(get("/cards?sort=player.name,desc"))
            .andExpect(status().isOk)
            .andExpect(view().name("cards"))
            .andExpect(model().attribute("currentSortProperty", "player.name"))
            .andExpect(model().attribute("currentSortDirection", "DESC"))
    }

    @Test
    fun `getCards with filter parameters should call service with correct arguments`() {
        val cards = emptyList<Card>()
        val cardPage = PageImpl(cards)

        whenever(cardService.getCardsFiltered(
            eq(1L),
            eq(2L),
            eq(3L),
            eq(4L),
            eq(5L),
            eq(6L),
            eq(true),
            eq(false),
            eq(7L),
            eq(true),
            eq("1-10"),
            eq(8L),
            eq(true),
            any<Pageable>()
        )).thenReturn(cardPage)

        mockMvc.perform(get("/cards")
            .param("manufacturerId", "1")
            .param("brandId", "2")
            .param("themeId", "3")
            .param("sportId", "4")
            .param("playerId", "5")
            .param("seasonId", "6")
            .param("gameUsed", "true")
            .param("autograph", "false")
            .param("variantId", "7")
            .param("rookieCard", "true")
            .param("printRunRangeKey", "1-10")
            .param("teamId", "8")
            .param("isGradedNullable", "true")
        )
            .andExpect(status().isOk)
            .andExpect(view().name("cards"))
    }
}
