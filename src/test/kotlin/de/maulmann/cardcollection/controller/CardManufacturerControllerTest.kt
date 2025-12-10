package de.maulmann.cardcollection.controller

import com.fasterxml.jackson.databind.ObjectMapper
import de.maulmann.cardcollection.model.*
import de.maulmann.cardcollection.service.CardManufacturerService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@AutoConfigureMockMvc
class CardManufacturerControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var cardManufacturerService: CardManufacturerService

    @Test
    fun `getCardsByManufacturerId should return cards for a given manufacturer`() {
        val manufacturerId = 1L
        val sport = Sport(name = "Basketball")
        val team = Team(name = "Lakers")
        val player = Player(name = "LeBron", surname = "James", sport = sport, team = team)
        val season = Season(name = "2022-23")
        val variant = Variant(name = "Silver Prizm")
        val manufacturer = CardManufacturer(name = "Panini")
        val brand = CardBrand(name = "Prizm", manufacturer = manufacturer)
        val theme = CardTheme(name = "Base Set", brand = brand)
        val cards = listOf(
            Card(number = "23", autograph = false, gameUsedMaterial = false, rookieCard = false, printRun = 100, serialNumber = 1, player = player, season = season, variant = variant, theme = theme),
            Card(number = "24", autograph = true, gameUsedMaterial = false, rookieCard = true, printRun = 50, serialNumber = 2, player = player, season = season, variant = variant, theme = theme)
        )

        whenever(cardManufacturerService.getCardsByManufacturerId(manufacturerId)).thenReturn(cards)

        mockMvc.perform(get("/api/cards/manufacturer/{manufacturerId}", manufacturerId))
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.length()").value(cards.size))
            .andExpect(jsonPath("$[0].number").value(cards[0].number))
            .andExpect(jsonPath("$[1].autograph").value(cards[1].autograph))
    }

    @Test
    fun `getCardsByManufacturerId should return empty list when no cards found`() {
        val manufacturerId = 2L
        whenever(cardManufacturerService.getCardsByManufacturerId(manufacturerId)).thenReturn(emptyList())

        mockMvc.perform(get("/api/cards/manufacturer/{manufacturerId}", manufacturerId))
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.length()").value(0))
    }
}
