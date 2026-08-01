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

    @GetMapping("/html")
    fun exportHtml(response: HttpServletResponse) {
        response.contentType = "application/zip"
        response.setHeader("Content-Disposition", "attachment; filename=\"seasons_export.zip\"")

        val seasons = seasonRepository.findAllByOrderByNameAsc()

        val seasonExports = Executors.newVirtualThreadPerTaskExecutor().use { executor ->
            seasons.map { season ->
                executor.submit(Callable {
                    val cards = cardRepository.findAllBySeasonIdWithDetails(season.id)
                    if (cards.isEmpty()) null
                    else season.name to buildSeasonHtml(cards)
                })
            }.mapNotNull { it.get() }
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
            val printRun = card.printRun?.toString() ?: ""
            val rookie = if (card.rookieCard) "Yes" else "No"
            val gameUsed = if (card.gameUsedMaterial) "Yes" else "No"
            val autograph = if (card.autograph) "Yes" else "No"
            val gradeStr = card.grading?.let { HtmlUtils.htmlEscape(it.displayGrade) } ?: ""

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
                <td>$printRun</td>
                <td>$rookie</td>
                <td>$gameUsed</td>
                <td>$autograph</td>
                <td>$gradeStr</td>
            </tr>
            """.trimIndent()
        }

        return """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <link rel="stylesheet" type="text/css" href="../css/main.css"/>
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