package de.maulmann.cardcollection.service

import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

/** Writes the serialised `cards.json` via a temp file and an atomic move so readers never see a partial file. */
object CardsJsonFileWriter {

    private val logger = LoggerFactory.getLogger(CardsJsonFileWriter::class.java)

    fun writeAtomically(json: ByteArray, cardCount: Int, file: File) {
        val parent = file.parentFile
        var tempFile: File? = null
        try {
            parent?.mkdirs()
            tempFile = File.createTempFile("cards_", ".json.tmp", parent ?: File("."))
            tempFile.writeBytes(json)
            try {
                Files.move(
                    tempFile.toPath(),
                    file.toPath(),
                    StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING
                )
            } catch (e: Exception) {
                // Fallback to non-atomic replace if atomic move is not supported across filesystem boundaries
                Files.move(tempFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING)
            }
            logger.info("Successfully synced cards.json [{} cards] to {}", cardCount, file.absolutePath)
        } finally {
            tempFile?.let { Files.deleteIfExists(it.toPath()) }
        }
    }
}
