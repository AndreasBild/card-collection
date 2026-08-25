package de.maulmann.cardcollection.service

import de.maulmann.cardcollection.model.*
import de.maulmann.cardcollection.repository.CardRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.io.File
import java.nio.file.Path
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GradingScanDownloadServiceTest {

    @TempDir
    lateinit var tempDir: Path

    private lateinit var cardRepository: CardRepository
    private lateinit var service: GradingScanDownloadService

    private val season = Season(id = 1L, name = "1997-98")
    private val manufacturer = CardManufacturer(id = 1L, name = "Fleer")
    private val brand = CardBrand(id = 1L, name = "Fleer Metal Universe")
    private val theme = CardTheme(id = 1L, name = "Base Set")
    private val variant = Variant(id = 1L, name = "Base")

    @BeforeEach
    fun setUp() {
        cardRepository = mock()
        service = GradingScanDownloadService(
            cardRepository = cardRepository,
            downloadDirSetting = tempDir.toString()
        )
    }

    @Test
    fun `test downloadDirectory creates directory if not exists`() {
        val subDir = tempDir.resolve("nested/gradingImages").toFile()
        val customService = GradingScanDownloadService(
            cardRepository = cardRepository,
            downloadDirSetting = subDir.absolutePath
        )

        val dir = customService.getDownloadDirectory()
        assertTrue(dir.exists())
        assertTrue(dir.isDirectory)
    }

    @Test
    fun `test card without cert number returns NO_CERT status`() {
        val grading = Grading(id = 1L, grade = 10.0f, gradingCompany = GradingCompany.PSA)
        val card = Card(
            id = 1L,
            season = season,
            manufacturer = manufacturer,
            brand = brand,
            variant = variant,
            theme = theme,
            number = "1",
            serialNumber = 0,
            rookieCard = false,
            gameUsedMaterial = false,
            autograph = false,
            grading = grading,
            gradingCertNumber = null
        )

        val result = service.downloadScansForCard(card, tempDir.toFile(), overwrite = false)
        assertEquals("NO_CERT", result.status)
        assertFalse(result.frontImageDownloaded)
        assertFalse(result.backImageDownloaded)
    }

    @Test
    fun `test card without grading company returns NO_COMPANY status`() {
        val card = Card(
            id = 2L,
            season = season,
            manufacturer = manufacturer,
            brand = brand,
            variant = variant,
            theme = theme,
            number = "2",
            serialNumber = 0,
            rookieCard = false,
            gameUsedMaterial = false,
            autograph = false,
            grading = null,
            gradingCertNumber = "12345678"
        )

        val result = service.downloadScansForCard(card, tempDir.toFile(), overwrite = false)
        assertEquals("NO_COMPANY", result.status)
    }

    @Test
    fun `test existing files on disk skip download when overwrite is false`() {
        val cert = "99887766"
        val frontFile = File(tempDir.toFile(), "PSA_${cert}_front.jpg")
        val backFile = File(tempDir.toFile(), "PSA_${cert}_back.jpg")
        frontFile.writeText("dummy-front-content")
        backFile.writeText("dummy-back-content")

        val grading = Grading(id = 1L, grade = 9.0f, gradingCompany = GradingCompany.PSA)
        val card = Card(
            id = 3L,
            season = season,
            manufacturer = manufacturer,
            brand = brand,
            variant = variant,
            theme = theme,
            number = "3",
            serialNumber = 0,
            rookieCard = false,
            gameUsedMaterial = false,
            autograph = false,
            grading = grading,
            gradingCertNumber = cert
        )

        val result = service.downloadScansForCard(card, tempDir.toFile(), overwrite = false)
        assertEquals("ALREADY_EXISTS", result.status)
        assertTrue(result.frontImageDownloaded)
        assertTrue(result.backImageDownloaded)
        assertEquals(frontFile.absolutePath, result.frontImagePath)
        assertEquals(backFile.absolutePath, result.backImagePath)
    }

    @Test
    fun `test downloadAllGradingScans handles empty list and returns valid summary`() {
        whenever(cardRepository.findAllWithDetails()).thenReturn(emptyList())

        val summary = service.downloadAllGradingScans(overwrite = false)
        assertEquals(0, summary.totalGradedCards)
        assertEquals(0, summary.successfulDownloads)
        assertEquals(0, summary.alreadyPresent)
        assertEquals(0, summary.notAvailableOrFailed)
        assertEquals(tempDir.toFile().absolutePath, summary.outputDirectory)
        assertTrue(summary.cardResults.isEmpty())
    }

    @Test
    fun `test resolveScanUrls handles non-existent cert gracefully`() {
        val (front, back) = service.resolveScanUrls(GradingCompany.PSA, "00000000_non_existent_cert")
        // Non-existent certificate won't crash and should return nulls or candidate URLs
    }
}
