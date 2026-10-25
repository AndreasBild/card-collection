package de.maulmann.cardcollection.service

import de.maulmann.cardcollection.dto.CardJsonDto
import java.io.BufferedWriter
import java.io.OutputStream

/** RFC 4180 CSV rendering of the export DTOs (CRLF line ends, quotes doubled, fields quoted when needed). */
object CardCsvWriter {

    private const val HEADER =
        "ID,Player,Season,Team,Company,Brand,Theme,Variant,Card Number,Serial Number,Print Run,Ratio," +
            "Grading Company,Grade,Grading Cert Number,Autograph,Patch,Rookie,Collection\r\n"

    fun write(dtos: List<CardJsonDto>, outputStream: OutputStream) {
        BufferedWriter(outputStream.writer(Charsets.UTF_8)).use { writer ->
            writer.write(HEADER)
            for (dto in dtos) {
                val row = listOf(
                    dto.id,
                    dto.player ?: "",
                    dto.season ?: "",
                    dto.team ?: "",
                    dto.company ?: "",
                    dto.brand ?: "",
                    dto.theme ?: "",
                    dto.variant ?: "",
                    dto.cardNumber ?: "",
                    dto.serialNumber ?: "",
                    dto.printRun?.toString() ?: "",
                    dto.packOdds ?: "",
                    dto.gradingCompany ?: "",
                    dto.grade ?: "",
                    dto.gradingCertNumber ?: "",
                    if (dto.isAutograph) "Yes" else "No",
                    if (dto.isPatch) "Yes" else "No",
                    if (dto.isRookie) "Yes" else "No",
                    dto.collection ?: ""
                ).joinToString(",") { escape(it) }
                writer.write(row)
                writer.write("\r\n")
            }
            writer.flush()
        }
    }

    internal fun escape(value: String): String {
        if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            return "\"" + value.replace("\"", "\"\"") + "\""
        }
        return value
    }
}
