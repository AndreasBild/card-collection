package de.maulmann.cardcollection.controller

import de.maulmann.cardcollection.repository.CardRepository
import de.maulmann.cardcollection.repository.SeasonRepository
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.OutputStreamWriter
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

@RestController
@RequestMapping("/export")
class ExportController(
    private val seasonRepository: SeasonRepository,
    private val cardRepository: CardRepository
) {

    @GetMapping("/html")
    fun exportHtml(response: HttpServletResponse) {
        response.contentType = "application/zip"
        response.setHeader("Content-Disposition", "attachment; filename=\"seasons_export.zip\"")

        ZipOutputStream(response.outputStream).use { zos ->
            val seasons = seasonRepository.findAllByOrderByNameAsc()
            var hasEntries = false
            for (season in seasons) {
                val cards = cardRepository.findAllBySeasonIdWithDetails(season.id)
                if (cards.isEmpty()) continue

                hasEntries = true
                val entry = ZipEntry("${season.name}.html")
                zos.putNextEntry(entry)

                val writer = OutputStreamWriter(zos)
                writer.write("<!DOCTYPE html>\n")
                writer.write("<html lang=\"en\">\n")
                writer.write("<head>\n")
                writer.write("    <meta charset=\"UTF-8\">\n")
                writer.write("    <link rel=\"stylesheet\" type=\"text/css\" href=\"../css/main.css\"/>\n")
                writer.write("</head>\n")
                writer.write("<body><h2>Juwan Howard Collection [Total: ${cards.size}]</h2>\n")
                writer.write("<table>\n")
                writer.write("    <tr>\n")
                writer.write("        <th>Player</th>\n")
                writer.write("        <th>Team</th>\n")
                writer.write("        <th>Sport</th>\n")
                writer.write("        <th>Season</th>\n")
                writer.write("        <th>Company</th>\n")
                writer.write("        <th>Brand</th>\n")
                writer.write("        <th>Theme</th>\n")
                writer.write("        <th>Variant</th>\n")
                writer.write("        <th>Number</th>\n")
                writer.write("        <th>Serial</th>\n")
                writer.write("        <th>Print Run</th>\n")
                writer.write("        <th>Rookie</th>\n")
                writer.write("        <th>Game Used</th>\n")
                writer.write("        <th>Autograph</th>\n")
                writer.write("        <th>Grade</th>\n")
                writer.write("    </tr>\n")

                for (card in cards) {
                    val playerName = "${card.player.name} ${card.player.surname}".trim()
                    val teamName = card.team?.name ?: ""
                    val sportName = card.player.sport?.name ?: ""
                    val seasonName = card.season.name
                    val companyName = card.theme.brand?.manufacturer?.name ?: ""
                    val brandName = card.theme.brand?.name ?: ""
                    val themeName = card.theme.name ?: ""
                    val variantName = card.variant.name ?: ""
                    val number = card.number
                    val serial = card.serialNumber
                    val printRun = card.printRun
                    val rookie = if (card.rookieCard) "Yes" else "No"
                    val gameUsed = if (card.gameUsedMaterial) "Yes" else "No"
                    val autograph = if (card.autograph) "Yes" else "No"
                    val gradeStr = card.grading?.let { "${it.gradingCompany?.name ?: ""} ${it.grade ?: ""}".trim() } ?: ""

                    writer.write("    <tr>\n")
                    writer.write("        <td>$playerName</td>\n")
                    writer.write("        <td>$teamName</td>\n")
                    writer.write("        <td>$sportName</td>\n")
                    writer.write("        <td>$seasonName</td>\n")
                    writer.write("        <td>$companyName</td>\n")
                    writer.write("        <td>$brandName</td>\n")
                    writer.write("        <td>$themeName</td>\n")
                    writer.write("        <td>$variantName</td>\n")
                    writer.write("        <td>$number</td>\n")
                    writer.write("        <td>$serial</td>\n")
                    writer.write("        <td>$printRun</td>\n")
                    writer.write("        <td>$rookie</td>\n")
                    writer.write("        <td>$gameUsed</td>\n")
                    writer.write("        <td>$autograph</td>\n")
                    writer.write("        <td>$gradeStr</td>\n")
                    writer.write("    </tr>\n")
                }

                writer.write("</table>\n")
                writer.write("</body>\n")
                writer.write("</html>")

                writer.flush()
                zos.closeEntry()
            }
            if (!hasEntries) {
                val emptyEntry = ZipEntry("empty.txt")
                zos.putNextEntry(emptyEntry)
                val writer = OutputStreamWriter(zos)
                writer.write("No cards found to export.")
                writer.flush()
                zos.closeEntry()
            }
        }
    }
}
