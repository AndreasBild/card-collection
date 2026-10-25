package de.maulmann.cardcollection.service

import de.maulmann.cardcollection.dto.CardJsonDto
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream

class CardCsvWriterTest {

    @Test
    fun `escape quotes fields with commas quotes and newlines`() {
        assertThat(CardCsvWriter.escape("plain")).isEqualTo("plain")
        assertThat(CardCsvWriter.escape("a,b")).isEqualTo("\"a,b\"")
        assertThat(CardCsvWriter.escape("say \"hi\"")).isEqualTo("\"say \"\"hi\"\"\"")
        assertThat(CardCsvWriter.escape("line1\nline2")).isEqualTo("\"line1\nline2\"")
        assertThat(CardCsvWriter.escape("line1\rline2")).isEqualTo("\"line1\rline2\"")
    }

    @Test
    fun `write emits header and CRLF terminated rows`() {
        val dto = CardJsonDto(
            id = "slug-1",
            player = "Juwan Howard, Other",
            season = "1994-95",
            team = null,
            company = "Fleer",
            brand = "Ultra",
            theme = null,
            variant = null,
            cardNumber = "1",
            serialNumber = null,
            printRun = null,
            gradingCompany = null,
            grade = null,
            isAutograph = true,
            isPatch = false,
            isRookie = true,
            collection = "Juwan Howard"
        )
        val out = ByteArrayOutputStream()

        CardCsvWriter.write(listOf(dto), out)

        val lines = out.toString(Charsets.UTF_8).split("\r\n")
        assertThat(lines[0]).startsWith("ID,Player,Season,Team")
        assertThat(lines[1]).startsWith("slug-1,\"Juwan Howard, Other\",1994-95,")
        assertThat(lines[1]).contains(",Yes,No,Yes,")
        assertThat(lines[2]).isEmpty()
    }
}
