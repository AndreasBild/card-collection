package de.maulmann.cardcollection.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import de.maulmann.cardcollection.dto.CardJsonDto
import de.maulmann.cardcollection.model.Card
import de.maulmann.cardcollection.model.FEATURED_PLAYER_NAME
import de.maulmann.cardcollection.model.GradingCompany
import de.maulmann.cardcollection.repository.CardRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.BufferedWriter
import java.io.File
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.text.Normalizer
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap

// Not transactional on purpose: findAllWithDetails() loads everything via fetch joins, so no DB connection
// is held while serialising, streaming to the client or writing files.
@Service
class CardExportService(
    private val cardRepository: CardRepository,
    private val objectMapper: ObjectMapper = ObjectMapper().apply {
        enable(SerializationFeature.INDENT_OUTPUT)
    },
    @param:Value("\${export.json.sync-path:../card-collectionJava/content/json/cards.json}")
    var syncPath: String = "../card-collectionJava/content/json/cards.json"
) {

    private val logger = LoggerFactory.getLogger(CardExportService::class.java)

    fun exportAllCardsToJsonDtos(): List<CardJsonDto> {
        val cards = cardRepository.findAllWithDetails()
        val existingSlugs = mutableSetOf<String>()
        return cards.map { mapToCardJsonDto(it, existingSlugs) }
    }

    fun mapToCardJsonDto(card: Card, existingSlugs: MutableSet<String> = mutableSetOf()): CardJsonDto {
        val people = card.peopleSummary()
        val playerStr = people.playerNames.ifBlank { null }
        val seasonStr = card.season.name.ifBlank { null }
        val teamStr = people.teamNames.ifBlank { null }
        val companyStr = card.manufacturer.name.ifBlank { null }
        val brandStr = card.brand.name.ifBlank { null }
        val themeStr = card.theme.name.ifBlank { null }
        val variantStr = card.variant.name.ifBlank { null }
        val cardNumberStr = card.number.ifBlank { null }

        val serialNumStr = if (card.serialNumber != 0) card.serialNumber.toString() else null
        val printRunVal = card.printRun
        val packOddsVal = card.packOdds?.trim()?.ifBlank { null }

        val gradingCompStr = card.grading?.gradingCompany?.name
        val gradeStr = card.grading?.grade?.let { gradeVal ->
            if (gradeVal % 1.0f == 0.0f) {
                gradeVal.toInt().toString()
            } else {
                gradeVal.toString()
            }
        }
        val gradingCertNumStr = card.gradingCertNumber?.trim()?.ifBlank { null }
        val gradingUrlStr = card.verificationUrl

        val collectionStr = playerStr ?: people.sportNames.ifBlank { null } ?: FEATURED_PLAYER_NAME
        val slugId = generateUniqueSlug(card, existingSlugs)

        return CardJsonDto(
            id = slugId,
            player = playerStr,
            season = seasonStr,
            team = teamStr,
            company = companyStr,
            brand = brandStr,
            theme = themeStr,
            variant = variantStr,
            cardNumber = cardNumberStr,
            serialNumber = serialNumStr,
            printRun = printRunVal,
            packOdds = packOddsVal,
            gradingCompany = gradingCompStr,
            grade = gradeStr,
            gradingCertNumber = gradingCertNumStr,
            gradingUrl = gradingUrlStr,
            isAutograph = card.autograph,
            isPatch = card.gameUsedMaterial,
            isRookie = card.rookieCard,
            collection = collectionStr,
            notes = null
        )
    }

    fun writeCardsJson(outputStream: OutputStream) {
        writeCardsJson(outputStream, File(syncPath))
    }

    fun writeCardsJson(outputStream: OutputStream, syncFile: File?) {
        val dtos = exportAllCardsToJsonDtos()
        // Serialise once and reuse the bytes for both the HTTP response and the sync file.
        val json = objectMapper.writeValueAsBytes(dtos)
        outputStream.write(json)
        outputStream.flush()
        syncFile?.let { file ->
            // Best-effort side effect: the HTTP response has already been written at this point.
            try {
                writeBytesAtomically(json, dtos.size, file)
            } catch (e: Exception) {
                logger.warn("Could not sync cards.json to {}: {}", file.absolutePath, e.message)
            }
        }
    }

    fun syncCardsJsonToStaticSite(dtos: List<CardJsonDto>? = null): File {
        return syncCardsJsonToStaticSite(File(syncPath), dtos)
    }

    fun syncCardsJsonToStaticSite(targetFile: File, dtos: List<CardJsonDto>? = null): File {
        val resolvedDtos = dtos ?: exportAllCardsToJsonDtos()
        syncDtosToFile(resolvedDtos, targetFile)
        return targetFile
    }

    fun exportCardsToJsonFile(file: File) {
        syncCardsJsonToStaticSite(file)
    }

    private fun syncDtosToFile(dtos: List<CardJsonDto>, file: File) {
        writeBytesAtomically(objectMapper.writeValueAsBytes(dtos), dtos.size, file)
    }

    private fun writeBytesAtomically(json: ByteArray, cardCount: Int, file: File) {
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

    fun writeCardsCsv(outputStream: OutputStream) {
        val dtos = exportAllCardsToJsonDtos()
        BufferedWriter(outputStream.writer(Charsets.UTF_8)).use { writer ->
            writer.write("ID,Player,Season,Team,Company,Brand,Theme,Variant,Card Number,Serial Number,Print Run,Ratio,Grading Company,Grade,Grading Cert Number,Autograph,Patch,Rookie,Collection\r\n")
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
                ).joinToString(",") { escapeCsv(it) }
                writer.write(row)
                writer.write("\r\n")
            }
            writer.flush()
        }
    }

    private fun escapeCsv(value: String): String {
        if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            return "\"" + value.replace("\"", "\"\"") + "\""
        }
        return value
    }

    private fun generateUniqueSlug(card: Card, existingSlugs: MutableSet<String>): String {
        val rawParts = mutableListOf<String>()

        card.season.name.takeIf { it.isNotBlank() }?.let { rawParts.add(it) }
        card.brand.name.takeIf { it.isNotBlank() }?.let { rawParts.add(it) }

        card.theme.name.takeIf { it.isNotBlank() && !it.equals("Base Set", ignoreCase = true) }?.let {
            rawParts.add(it)
        }

        card.variant.name.takeIf { it.isNotBlank() && !it.equals("Base", ignoreCase = true) }?.let {
            rawParts.add(it)
        }

        card.number.takeIf { it.isNotBlank() }?.let { rawParts.add(it) }

        if (card.serialNumber != 0) {
            rawParts.add("sn${card.serialNumber}")
        }

        val baseSlug = rawParts.joinToString("-") { toSlug(it) }.ifBlank { "card" }

        var candidateSlug = baseSlug
        if (existingSlugs.contains(candidateSlug)) {
            candidateSlug = "$baseSlug-card${card.id}"
        }
        var index = 2
        while (existingSlugs.contains(candidateSlug)) {
            candidateSlug = "$baseSlug-card${card.id}-$index"
            index++
        }

        existingSlugs.add(candidateSlug)
        return candidateSlug
    }

    private val slugCache = ConcurrentHashMap<String, String>()

    private fun toSlug(input: String): String {
        return slugCache.computeIfAbsent(input) { raw ->
            val normalized = Normalizer.normalize(raw, Normalizer.Form.NFD)
            val ascii = DIACRITICS_REGEX.replace(normalized, "")
            NON_ALPHANUMERIC_REGEX.replace(ascii.lowercase(Locale.ENGLISH), "-")
                .replace(CONSECUTIVE_DASHES_REGEX, "-")
                .trim('-')
        }
    }

    companion object {
        private val DIACRITICS_REGEX = Regex("\\p{InCombiningDiacriticalMarks}+")
        private val NON_ALPHANUMERIC_REGEX = Regex("[^a-z0-9]+")
        private val CONSECUTIVE_DASHES_REGEX = Regex("-+")
    }
}
