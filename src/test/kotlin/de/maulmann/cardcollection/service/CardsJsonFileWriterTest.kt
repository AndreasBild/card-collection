package de.maulmann.cardcollection.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class CardsJsonFileWriterTest {

    @TempDir
    lateinit var tempDir: File

    @Test
    fun `writeAtomically creates parent directories and replaces existing content without leaving temp files`() {
        val target = File(tempDir, "nested/dir/cards.json")

        CardsJsonFileWriter.writeAtomically("[1]".toByteArray(), 1, target)
        assertThat(target.readText()).isEqualTo("[1]")

        CardsJsonFileWriter.writeAtomically("[1,2]".toByteArray(), 2, target)
        assertThat(target.readText()).isEqualTo("[1,2]")
        assertThat(target.parentFile.listFiles()!!.map { it.name }).containsExactly("cards.json")
    }
}
