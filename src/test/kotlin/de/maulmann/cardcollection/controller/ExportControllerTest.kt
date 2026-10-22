package de.maulmann.cardcollection.controller

import de.maulmann.cardcollection.dto.GradingScanDownloadSummary
import de.maulmann.cardcollection.service.CardExportService
import de.maulmann.cardcollection.service.GradingScanDownloadService
import de.maulmann.cardcollection.service.HtmlExportService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.mock.web.MockHttpServletResponse
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class ExportControllerTest {

    @Mock
    private lateinit var htmlExportService: HtmlExportService

    @Mock
    private lateinit var cardExportService: CardExportService

    @Mock
    private lateinit var gradingScanDownloadService: GradingScanDownloadService

    @InjectMocks
    private lateinit var exportController: ExportController

    @Test
    fun `test exportJson sets headers and invokes CardExportService`() {
        val response = MockHttpServletResponse()

        exportController.exportJson(response)

        assertEquals("application/json;charset=UTF-8", response.contentType)
        assertEquals("attachment; filename=\"cards.json\"", response.getHeader("Content-Disposition"))
        verify(cardExportService).writeCardsJson(response.outputStream)
    }

    @Test
    fun `test syncJsonToStaticSite invokes CardExportService and returns response`() {
        val dummyFile = java.io.File("content/json/cards.json")
        `when`(cardExportService.exportAllCardsToJsonDtos()).thenReturn(emptyList())
        `when`(cardExportService.syncCardsJsonToStaticSite(emptyList())).thenReturn(dummyFile)

        val response = exportController.syncJsonToStaticSite()

        assertEquals(org.springframework.http.HttpStatus.OK, response.statusCode)
        assertEquals("success", response.body?.status)
        assertEquals(dummyFile.absolutePath, response.body?.syncedFile)
        assertEquals(0, response.body?.cardCount)
    }

    @Test
    fun `test downloadGradingScans invokes GradingScanDownloadService and returns summary`() {
        val dummySummary = GradingScanDownloadSummary(
            totalGradedCards = 2,
            successfulDownloads = 2,
            alreadyPresent = 0,
            notAvailableOrFailed = 0,
            outputDirectory = "gradingImages",
            cardResults = emptyList()
        )
        `when`(gradingScanDownloadService.downloadAllGradingScans(false)).thenReturn(dummySummary)

        val response = exportController.downloadGradingScans(overwrite = false)

        assertEquals(org.springframework.http.HttpStatus.OK, response.statusCode)
        assertEquals(2, response.body?.totalGradedCards)
        assertEquals(2, response.body?.successfulDownloads)
    }

    @Test
    fun `test exportCsv sets headers and invokes CardExportService`() {
        val response = MockHttpServletResponse()

        exportController.exportCsv(response)

        assertEquals("text/csv;charset=UTF-8", response.contentType)
        assertEquals("attachment; filename=\"card-collection.csv\"", response.getHeader("Content-Disposition"))
        verify(cardExportService).writeCardsCsv(response.outputStream)
    }

    @Test
    fun `test exportHtml sets zip headers and delegates to HtmlExportService`() {
        val response = MockHttpServletResponse()

        exportController.exportHtml(response)

        assertEquals("application/zip", response.contentType)
        assertEquals("attachment; filename=\"seasons_export.zip\"", response.getHeader("Content-Disposition"))
        verify(htmlExportService).writeSeasonsZip(response.outputStream)
    }
}