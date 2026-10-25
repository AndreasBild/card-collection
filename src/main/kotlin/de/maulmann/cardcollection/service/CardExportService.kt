package de.maulmann.cardcollection.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import de.maulmann.cardcollection.dto.CardJsonDto
import de.maulmann.cardcollection.model.Card
import de.maulmann.cardcollection.model.FeaturedPlayer
import de.maulmann.cardcollection.repository.CardRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File
import java.io.OutputStream

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

        val collectionStr = playerStr ?: people.sportNames.ifBlank { null } ?: FeaturedPlayer.name
        val slugId = CardSlugGenerator.generateUnique(card, existingSlugs)

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
                CardsJsonFileWriter.writeAtomically(json, dtos.size, file)
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
        CardsJsonFileWriter.writeAtomically(objectMapper.writeValueAsBytes(dtos), dtos.size, file)
    }

    fun writeCardsCsv(outputStream: OutputStream) {
        CardCsvWriter.write(exportAllCardsToJsonDtos(), outputStream)
    }
}
