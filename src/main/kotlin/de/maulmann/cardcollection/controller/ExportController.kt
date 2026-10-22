package de.maulmann.cardcollection.controller

import de.maulmann.cardcollection.dto.GradingScanDownloadSummary
import de.maulmann.cardcollection.dto.SyncStatusResponse
import de.maulmann.cardcollection.service.CardExportService
import de.maulmann.cardcollection.service.GradingScanDownloadService
import de.maulmann.cardcollection.service.HtmlExportService
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/export")
class ExportController(
    private val htmlExportService: HtmlExportService,
    private val cardExportService: CardExportService,
    private val gradingScanDownloadService: GradingScanDownloadService
) {

    @RequestMapping(value = ["/grading-scans/download"], method = [RequestMethod.GET, RequestMethod.POST])
    fun downloadGradingScans(
        @RequestParam(defaultValue = "false") overwrite: Boolean
    ): ResponseEntity<GradingScanDownloadSummary> {
        val summary = gradingScanDownloadService.downloadAllGradingScans(overwrite)
        return ResponseEntity.ok(summary)
    }

    @GetMapping("/json")
    fun exportJson(response: HttpServletResponse) {
        response.contentType = "application/json;charset=UTF-8"
        response.setHeader("Content-Disposition", "attachment; filename=\"cards.json\"")
        cardExportService.writeCardsJson(response.outputStream)
    }

    @RequestMapping(value = ["/json/sync"], method = [RequestMethod.GET, RequestMethod.POST])
    fun syncJsonToStaticSite(): ResponseEntity<SyncStatusResponse> {
        val dtos = cardExportService.exportAllCardsToJsonDtos()
        val targetFile = cardExportService.syncCardsJsonToStaticSite(dtos = dtos)
        return ResponseEntity.ok(
            SyncStatusResponse(
                status = "success",
                syncedFile = targetFile.absolutePath,
                exists = targetFile.exists(),
                fileSizeBytes = targetFile.length(),
                cardCount = dtos.size
            )
        )
    }

    @GetMapping("/csv")
    fun exportCsv(response: HttpServletResponse) {
        response.contentType = "text/csv;charset=UTF-8"
        response.setHeader("Content-Disposition", "attachment; filename=\"card-collection.csv\"")
        cardExportService.writeCardsCsv(response.outputStream)
    }

    @GetMapping("/html")
    fun exportHtml(response: HttpServletResponse) {
        response.contentType = "application/zip"
        response.setHeader("Content-Disposition", "attachment; filename=\"seasons_export.zip\"")
        htmlExportService.writeSeasonsZip(response.outputStream)
    }
}
