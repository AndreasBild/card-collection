package de.maulmann.cardcollection.service

import com.fasterxml.jackson.databind.ObjectMapper
import de.maulmann.cardcollection.dto.CardJsonDto
import de.maulmann.cardcollection.model.*
import de.maulmann.cardcollection.repository.CardRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.io.TempDir
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.io.ByteArrayOutputStream
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
class CardExportServiceTest {

    @Mock
    private lateinit var cardRepository: CardRepository

    private lateinit var cardExportService: CardExportService

    @BeforeEach
    fun setUp() {
        cardExportService = CardExportService(cardRepository)
    }

    @Test
    fun `test mapToCardJsonDto maps all fields correctly`() {
        val season = Season(id = 1L, name = "1997-98")
        val manufacturer = CardManufacturer(id = 1L, name = "Fleer")
        val brand = CardBrand(id = 1L, name = "Fleer Metal Universe")
        val theme = CardTheme(id = 1L, name = "Base Set")
        val variant = Variant(id = 1L, name = "Precious Metal Gems Red")
        val sport = Sport(id = 1L, name = "Basketball")
        val team = Team(id = 1L, name = "Washington Bullets")
        val player = Player(id = 1L, name = "Juwan", surname = "Howard", sport = sport)

        val grading = Grading(id = 1L, grade = 9.0f, gradingCompany = GradingCompany.PSA)

        val cardRef = Card(
            id = 1L,
            season = season,
            manufacturer = manufacturer,
            brand = brand,
            variant = variant,
            theme = theme,
            number = "33",
            serialNumber = 47,
            printRun = 100,
            rookieCard = true,
            gameUsedMaterial = true,
            autograph = true,
            grading = grading
        )

        val cardPlayer = CardPlayer(
            id = CardPlayerId(1L, player.id),
            card = cardRef,
            player = player,
            team = team
        )

        val card = Card(
            id = 1L,
            season = season,
            manufacturer = manufacturer,
            brand = brand,
            variant = variant,
            theme = theme,
            number = "33",
            serialNumber = 47,
            printRun = 100,
            rookieCard = true,
            gameUsedMaterial = true,
            autograph = true,
            grading = grading,
            cardPlayers = mutableSetOf(cardPlayer)
        )

        val dto = cardExportService.mapToCardJsonDto(card)

        assertEquals("1997-98-fleer-metal-universe-precious-metal-gems-red-33-sn47", dto.id)
        assertEquals("Juwan Howard", dto.player)
        assertEquals("1997-98", dto.season)
        assertEquals("Washington Bullets", dto.team)
        assertEquals("Fleer", dto.company)
        assertEquals("Fleer Metal Universe", dto.brand)
        assertEquals("Base Set", dto.theme)
        assertEquals("Precious Metal Gems Red", dto.variant)
        assertEquals("33", dto.cardNumber)
        assertEquals("47", dto.serialNumber)
        assertEquals(100, dto.printRun)
        assertEquals("PSA", dto.gradingCompany)
        assertEquals("9", dto.grade)
        assertTrue(dto.isAutograph)
        assertTrue(dto.isPatch)
        assertTrue(dto.isRookie)
        assertEquals("Juwan Howard", dto.collection)
        assertNull(dto.notes)
    }

    @Test
    fun `test mapToCardJsonDto handles null printRun and serialNumber zero as null`() {
        val season = Season(id = 1L, name = "1994-95")
        val manufacturer = CardManufacturer(id = 1L, name = "Upper Deck")
        val brand = CardBrand(id = 1L, name = "Collectors Choice")
        val theme = CardTheme(id = 1L, name = "Base Set")
        val variant = Variant(id = 1L, name = "Base")
        val sport = Sport(id = 1L, name = "Basketball")
        val player = Player(id = 1L, name = "Juwan", surname = "Howard", sport = sport)

        val cardRef = Card(
            id = 2L,
            season = season,
            manufacturer = manufacturer,
            brand = brand,
            variant = variant,
            theme = theme,
            number = "278",
            serialNumber = 0,
            printRun = null,
            rookieCard = false,
            gameUsedMaterial = false,
            autograph = false,
            grading = null
        )

        val cardPlayer = CardPlayer(
            id = CardPlayerId(2L, player.id),
            card = cardRef,
            player = player,
            team = null
        )

        val card = Card(
            id = 2L,
            season = season,
            manufacturer = manufacturer,
            brand = brand,
            variant = variant,
            theme = theme,
            number = "278",
            serialNumber = 0,
            printRun = null,
            rookieCard = false,
            gameUsedMaterial = false,
            autograph = false,
            grading = null,
            cardPlayers = mutableSetOf(cardPlayer)
        )

        val dto = cardExportService.mapToCardJsonDto(card)

        assertEquals("1994-95-collectors-choice-278", dto.id)
        assertNull(dto.serialNumber)
        assertNull(dto.printRun)
        assertNull(dto.gradingCompany)
        assertNull(dto.grade)
        assertNull(dto.team)
        assertFalse(dto.isAutograph)
        assertFalse(dto.isPatch)
        assertFalse(dto.isRookie)
    }

    @Test
    fun `test mapToCardJsonDto handles negative serialNumber correctly`() {
        val season = Season(id = 1L, name = "1994-95")
        val manufacturer = CardManufacturer(id = 1L, name = "Upper Deck")
        val brand = CardBrand(id = 1L, name = "Collectors Choice")
        val theme = CardTheme(id = 1L, name = "Base Set")
        val variant = Variant(id = 1L, name = "Base")
        val sport = Sport(id = 1L, name = "Basketball")
        val player = Player(id = 1L, name = "Juwan", surname = "Howard", sport = sport)

        val cardRef = Card(
            id = 2L,
            season = season,
            manufacturer = manufacturer,
            brand = brand,
            variant = variant,
            theme = theme,
            number = "278",
            serialNumber = -4,
            printRun = null,
            rookieCard = false,
            gameUsedMaterial = false,
            autograph = false,
            grading = null
        )

        val cardPlayer = CardPlayer(
            id = CardPlayerId(2L, player.id),
            card = cardRef,
            player = player,
            team = null
        )

        val card = Card(
            id = 2L,
            season = season,
            manufacturer = manufacturer,
            brand = brand,
            variant = variant,
            theme = theme,
            number = "278",
            serialNumber = -4,
            printRun = null,
            rookieCard = false,
            gameUsedMaterial = false,
            autograph = false,
            grading = null,
            cardPlayers = mutableSetOf(cardPlayer)
        )

        val dto = cardExportService.mapToCardJsonDto(card)

        assertEquals("1994-95-collectors-choice-278-sn-4", dto.id)
        assertEquals("-4", dto.serialNumber)
        assertNull(dto.printRun)
    }

    @Test
    fun `test writeCardsJson produces valid JSON schema`() {
        val season = Season(id = 1L, name = "1997-98")
        val manufacturer = CardManufacturer(id = 1L, name = "Fleer")
        val brand = CardBrand(id = 1L, name = "Fleer Metal Universe")
        val theme = CardTheme(id = 1L, name = "Base Set")
        val variant = Variant(id = 1L, name = "Precious Metal Gems Red")

        val card = Card(
            id = 1L,
            season = season,
            manufacturer = manufacturer,
            brand = brand,
            variant = variant,
            theme = theme,
            number = "33",
            serialNumber = 47,
            printRun = 100,
            rookieCard = false,
            gameUsedMaterial = true,
            autograph = true,
            grading = null
        )

        `when`(cardRepository.findAllWithDetails()).thenReturn(listOf(card))

        val baos = ByteArrayOutputStream()
        cardExportService.writeCardsJson(baos)

        val jsonString = baos.toString("UTF-8")
        assertTrue(jsonString.contains("\"id\" : \"1997-98-fleer-metal-universe-precious-metal-gems-red-33-sn47\""))
        assertTrue(jsonString.contains("\"printRun\" : 100"))
        assertTrue(jsonString.contains("\"isAutograph\" : true"))
        assertTrue(jsonString.contains("\"isPatch\" : true"))
        assertTrue(jsonString.contains("\"isRookie\" : false"))
        assertTrue(jsonString.contains("\"serialNumber\" : \"47\""))
    }

    @Test
    fun `test exportCardsToJsonFile creates cards json file`(@TempDir tempDir: File) {
        val file = File(tempDir, "cards.json")
        val season = Season(id = 1L, name = "1994-95")
        val manufacturer = CardManufacturer(id = 1L, name = "Upper Deck")
        val brand = CardBrand(id = 1L, name = "Collectors Choice")
        val theme = CardTheme(id = 1L, name = "Base Set")
        val variant = Variant(id = 1L, name = "Base")

        val card = Card(
            id = 1L,
            season = season,
            manufacturer = manufacturer,
            brand = brand,
            variant = variant,
            theme = theme,
            number = "278",
            serialNumber = 0,
            printRun = null,
            rookieCard = false,
            gameUsedMaterial = false,
            autograph = false,
            grading = null
        )

        `when`(cardRepository.findAllWithDetails()).thenReturn(listOf(card))

        cardExportService.exportCardsToJsonFile(file)

        assertTrue(file.exists())
        val content = file.readText(Charsets.UTF_8)
        assertTrue(content.contains("\"id\" : \"1994-95-collectors-choice-278\""))
    }
}
