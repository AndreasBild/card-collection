package de.maulmann.cardcollection.controller

import de.maulmann.cardcollection.model.Card
import de.maulmann.cardcollection.repository.CardRepository
import de.maulmann.cardcollection.repository.SeasonRepository
import de.maulmann.cardcollection.service.CardExportService
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.HtmlUtils
import java.io.OutputStreamWriter
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

@RestController
@RequestMapping("/export")
class ExportController(
    private val seasonRepository: SeasonRepository,
    private val cardRepository: CardRepository,
    private val cardExportService: CardExportService
) {

    @GetMapping("/json")
    fun exportJson(response: HttpServletResponse) {
        response.contentType = "application/json;charset=UTF-8"
        response.setHeader("Content-Disposition", "attachment; filename=\"cards.json\"")
        cardExportService.writeCardsJson(response.outputStream)
    }

    @GetMapping("/json/sync")
    @org.springframework.web.bind.annotation.PostMapping("/json/sync")
    fun syncJsonToStaticSite(): org.springframework.http.ResponseEntity<de.maulmann.cardcollection.dto.SyncStatusResponse> {
        val dtos = cardExportService.exportAllCardsToJsonDtos()
        val targetFile = cardExportService.syncCardsJsonToStaticSite()
        return org.springframework.http.ResponseEntity.ok(
            de.maulmann.cardcollection.dto.SyncStatusResponse(
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

        val allCards = cardRepository.findAllWithDetails()
        val cardsBySeason = allCards.groupBy { it.season.name }.toSortedMap()

        val seasonExports = if (cardsBySeason.isEmpty()) {
            emptyList()
        } else {
            Executors.newVirtualThreadPerTaskExecutor().use { executor ->
                cardsBySeason.entries.map { (seasonName, cards) ->
                    executor.submit(Callable {
                        seasonName to buildSeasonHtml(cards)
                    })
                }.map { it.get() }
            }
        }

        ZipOutputStream(response.outputStream).use { zos ->
            if (seasonExports.isEmpty()) {
                val emptyEntry = ZipEntry("empty.txt")
                zos.putNextEntry(emptyEntry)
                val writer = OutputStreamWriter(zos)
                writer.write("No cards found to export.")
                writer.flush()
                zos.closeEntry()
            } else {
                for ((seasonName, htmlContent) in seasonExports) {
                    val entry = ZipEntry("$seasonName.html")
                    zos.putNextEntry(entry)
                    val writer = OutputStreamWriter(zos)
                    writer.write(htmlContent)
                    writer.flush()
                    zos.closeEntry()
                }
            }
        }
    }

    private fun buildSeasonHtml(cards: List<Card>): String {
        val rows = cards.joinToString("\n") { card ->
            val playerName = HtmlUtils.htmlEscape(card.playerNames)
            val teamName = HtmlUtils.htmlEscape(card.teamNames)
            val sportName = HtmlUtils.htmlEscape(card.sportNames)
            val seasonName = HtmlUtils.htmlEscape(card.season.name)
            val companyName = HtmlUtils.htmlEscape(card.manufacturer.name)
            val brandName = HtmlUtils.htmlEscape(card.brand.name)
            val themeName = HtmlUtils.htmlEscape(card.theme.name)
            val variantName = HtmlUtils.htmlEscape(card.variant.name)
            val number = HtmlUtils.htmlEscape(card.number)
            val serial = card.serialNumber
            val printRunHtml = when {
                card.printRun == 1 -> "<span class=\"badge badge-oneofone\">1/1</span>"
                card.printRun != null -> "/${card.printRun}"
                else -> ""
            }
            val rookieHtml = if (card.rookieCard) "<span class=\"badge badge-rc\">RC</span>" else "-"
            val gameUsedHtml = if (card.gameUsedMaterial) "<span class=\"badge badge-gu\">GU</span>" else "-"
            val autographHtml = if (card.autograph) "<span class=\"badge badge-auto\">AUTO</span>" else "-"
            val gradeHtml = card.grading?.let {
                val company = it.gradingCompany?.name ?: ""
                val badgeClass = when (company) {
                    "PSA" -> "badge-psa"
                    "BGS" -> "badge-bgs"
                    "MBA" -> "badge-mba"
                    "SGC" -> "badge-sgc"
                    else -> ""
                }
                val certHtml = if (!card.gradingCertNumber.isNullOrBlank()) {
                    val certEscaped = HtmlUtils.htmlEscape(card.gradingCertNumber!!.trim())
                    val url = card.verificationUrl
                    if (url != null) {
                        """ <a href="${HtmlUtils.htmlEscape(url)}" target="_blank" rel="noopener noreferrer" class="cert-link" title="Verify #${certEscaped}">#$certEscaped</a>"""
                    } else {
                        """ <span class="cert-pill">#$certEscaped</span>"""
                    }
                } else ""
                "<span class=\"badge-grading $badgeClass\">${HtmlUtils.htmlEscape(it.displayGrade)}</span>$certHtml"
            } ?: ""

            """
            <tr>
                <td>$playerName</td>
                <td>$teamName</td>
                <td>$sportName</td>
                <td>$seasonName</td>
                <td>$companyName</td>
                <td>$brandName</td>
                <td>$themeName</td>
                <td>$variantName</td>
                <td>$number</td>
                <td>$serial</td>
                <td>$printRunHtml</td>
                <td>$rookieHtml</td>
                <td>$gameUsedHtml</td>
                <td>$autographHtml</td>
                <td>$gradeHtml</td>
            </tr>
            """.trimIndent()
        }

        return """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1">
            <title>Juwan Howard Collection - Export</title>
            <style>
                body {
                    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
                    background-color: #0b0f19;
                    color: #f8fafc;
                    padding: 24px;
                    margin: 0;
                }
                h2 {
                    color: #f8fafc;
                    margin-bottom: 16px;
                }
                table {
                    width: 100%;
                    border-collapse: collapse;
                    background: #131b2e;
                    border-radius: 8px;
                    overflow: hidden;
                    border: 1px solid #1e293b;
                }
                th, td {
                    padding: 10px 14px;
                    text-align: left;
                    border-bottom: 1px solid #1e293b;
                    font-size: 0.875rem;
                }
                th {
                    background-color: #0f172a;
                    color: #94a3b8;
                    font-weight: 600;
                    text-transform: uppercase;
                    letter-spacing: 0.05em;
                }
                tr:hover {
                    background-color: #1e293b;
                }
                .badge {
                    display: inline-flex;
                    align-items: center;
                    justify-content: center;
                    font-size: 0.75rem;
                    font-weight: 700;
                    padding: 2px 8px;
                    border-radius: 4px;
                    letter-spacing: 0.03em;
                    text-transform: uppercase;
                    white-space: nowrap;
                    border: 1px solid transparent;
                }
                .badge-rc {
                    background-color: rgba(245, 158, 11, 0.2);
                    color: #fde68a;
                    border-color: rgba(245, 158, 11, 0.4);
                }
                .badge-auto {
                    background-color: rgba(139, 92, 246, 0.2);
                    color: #ddd6fe;
                    border-color: rgba(139, 92, 246, 0.4);
                }
                .badge-gu {
                    background-color: rgba(20, 184, 166, 0.2);
                    color: #99f6e4;
                    border-color: rgba(20, 184, 166, 0.4);
                }
                .badge-oneofone {
                    background: linear-gradient(135deg, #f59e0b, #ec4899, #8b5cf6);
                    color: #ffffff;
                    font-weight: 800;
                }
                .badge-grading {
                    display: inline-flex;
                    align-items: center;
                    font-size: 0.75rem;
                    font-weight: 700;
                    padding: 2px 8px;
                    border-radius: 12px;
                    border: 1px solid transparent;
                }
                .badge-psa {
                    background-color: rgba(239, 68, 68, 0.2);
                    color: #fca5a5;
                    border-color: rgba(239, 68, 68, 0.4);
                }
                .badge-bgs {
                    background-color: rgba(14, 165, 233, 0.2);
                    color: #7dd3fc;
                    border-color: rgba(14, 165, 233, 0.4);
                }
                .badge-mba {
                    background-color: rgba(168, 85, 247, 0.2);
                    color: #d8b4fe;
                    border-color: rgba(168, 85, 247, 0.4);
                }
                .badge-sgc {
                    background-color: rgba(148, 163, 184, 0.2);
                    color: #f1f5f9;
                    border-color: rgba(148, 163, 184, 0.4);
                }
                .cert-link {
                    font-size: 0.75rem;
                    color: #60a5fa;
                    text-decoration: none;
                    margin-left: 4px;
                    font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
                }
                .cert-link:hover {
                    text-decoration: underline;
                    color: #93c5fd;
                }
                .cert-pill {
                    font-size: 0.75rem;
                    color: #94a3b8;
                    margin-left: 4px;
                    font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
                }
            </style>
        </head>
        <body>
        <h2>Juwan Howard Collection [Total: ${cards.size}]</h2>
        <table>
            <tr>
                <th>Player</th>
                <th>Team</th>
                <th>Sport</th>
                <th>Season</th>
                <th>Company</th>
                <th>Brand</th>
                <th>Theme</th>
                <th>Variant</th>
                <th>Number</th>
                <th>Serial</th>
                <th>Print Run</th>
                <th>Rookie</th>
                <th>Game Used</th>
                <th>Autograph</th>
                <th>Grade</th>
            </tr>
        $rows
        </table>
        </body>
        </html>
        """.trimIndent()
    }
}