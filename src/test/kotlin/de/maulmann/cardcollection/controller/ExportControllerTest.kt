package de.maulmann.cardcollection.controller

import de.maulmann.cardcollection.model.*
import de.maulmann.cardcollection.repository.CardRepository
import de.maulmann.cardcollection.repository.SeasonRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.mock.web.MockHttpServletResponse
import java.util.zip.ZipInputStream
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
class ExportControllerTest {

    @Mock
    private lateinit var seasonRepository: SeasonRepository

    @Mock
    private lateinit var cardRepository: CardRepository

    @InjectMocks
    private lateinit var exportController: ExportController

    @Test
    fun `test exportHtml generates valid zip file`() {
        val season = Season(id = 1L, name = "1994-95")

        val manufacturer = CardManufacturer(id = 1L, name = "Upper Deck")
        val brand = CardBrand(id = 1L, name = "Collectors Choice", manufacturer = manufacturer)
        val theme = CardTheme(id = 1L, name = "Base Set", brand = brand)
        val sport = Sport(id = 1L, name = "Basketball")
        val team = Team(id = 1L, name = "Washington Bullets")
        val grading = Grading(
            id = 1L,
            grade = 9.0f,
            gradingCompany = GradingCompany.PSA
        )

        val playerWithAmpersand = Player(id = 2L, name = "Shaq", surname = "& Kobe", sport = sport)
        val variant = Variant(id = 1L, name = "Base")

        val cardRef = Card(
            id = 1L,
            season = season,
            variant = variant,
            theme = theme,
            number = "278",
            serialNumber = 0,
            printRun = 0,
            rookieCard = false,
            gameUsedMaterial = false,
            autograph = false,
            grading = grading
        )

        val cardPlayer = CardPlayer(
            id = CardPlayerId(1L, playerWithAmpersand.id),
            card = cardRef,
            player = playerWithAmpersand,
            team = team
        )

        val card = Card(
            id = 1L,
            season = season,
            variant = variant,
            theme = theme,
            number = "278",
            serialNumber = 0,
            printRun = 0,
            rookieCard = false,
            gameUsedMaterial = false,
            autograph = false,
            grading = grading,
            cardPlayers = setOf(cardPlayer)
        )

        `when`(seasonRepository.findAllByOrderByNameAsc()).thenReturn(listOf(season))
        `when`(cardRepository.findAllBySeasonIdWithDetails(1L)).thenReturn(listOf(card))

        val response = MockHttpServletResponse()

        exportController.exportHtml(response)

        assertEquals("application/zip", response.contentType)
        assertEquals("attachment; filename=\"seasons_export.zip\"", response.getHeader("Content-Disposition"))

        val content = response.contentAsByteArray
        assertTrue(content.isNotEmpty())

        val zis = ZipInputStream(content.inputStream())
        val entry = zis.nextEntry

        assertTrue(entry != null)
        assertEquals("1994-95.html", entry.name)

        val htmlContent = zis.bufferedReader().readText()
        assertTrue(htmlContent.contains("<h2>Juwan Howard Collection [Total: 1]</h2>"))
        assertTrue(htmlContent.contains("<td>Shaq &amp; Kobe</td>"))
        assertTrue(htmlContent.contains("<td>Washington Bullets</td>"))
        assertTrue(htmlContent.contains("<td>Basketball</td>"))
        assertTrue(htmlContent.contains("<td>1994-95</td>"))
        assertTrue(htmlContent.contains("<td>Upper Deck</td>"))
        assertTrue(htmlContent.contains("<td>Collectors Choice</td>"))
        assertTrue(htmlContent.contains("<td>Base Set</td>"))
        assertTrue(htmlContent.contains("<td>Base</td>"))
        assertTrue(htmlContent.contains("<td>278</td>"))
        assertTrue(htmlContent.contains("<td>PSA 9</td>"))
    }
}