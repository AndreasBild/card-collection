package de.maulmann.cardcollection.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import de.maulmann.cardcollection.dto.CardJsonDto
import de.maulmann.cardcollection.model.Card
import de.maulmann.cardcollection.model.GradingCompany
import de.maulmann.cardcollection.repository.CardRepository
import org.springframework.stereotype.Service
import java.io.File
import java.io.OutputStream
import java.text.Normalizer
import java.util.Locale

@Service
class CardExportService(
    private val cardRepository: CardRepository,
    private val objectMapper: ObjectMapper = ObjectMapper().apply {
        enable(SerializationFeature.INDENT_OUTPUT)
    }
) {

    fun exportAllCardsToJsonDtos(): List<CardJsonDto> {
        val cards = cardRepository.findAllWithDetails()
        val existingSlugs = mutableSetOf<String>()
        return cards.map { mapToCardJsonDto(it, existingSlugs) }
    }

    fun mapToCardJsonDto(card: Card, existingSlugs: MutableSet<String> = mutableSetOf()): CardJsonDto {
        val playerStr = card.playerNames.ifBlank { null }
        val seasonStr = card.season.name.ifBlank { null }
        val teamStr = card.teamNames.ifBlank { null }
        val companyStr = card.manufacturer.name.ifBlank { null }
        val brandStr = card.brand.name.ifBlank { null }
        val themeStr = card.theme.name.ifBlank { null }
        val variantStr = card.variant.name.ifBlank { null }
        val cardNumberStr = card.number.ifBlank { null }

        val serialNumStr = if (card.serialNumber > 0) card.serialNumber.toString() else null
        val printRunVal = if (card.printRun > 0) card.printRun else null

        val gradingCompStr = card.grading?.gradingCompany?.name
        val gradeStr = card.grading?.grade?.let { gradeVal ->
            if (card.grading?.gradingCompany == GradingCompany.PSA && gradeVal % 1.0f == 0.0f) {
                gradeVal.toInt().toString()
            } else if (gradeVal % 1.0f == 0.0f) {
                gradeVal.toInt().toString()
            } else {
                gradeVal.toString()
            }
        }

        val collectionStr = playerStr ?: card.sportNames.ifBlank { null } ?: "Juwan Howard"
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
            gradingCompany = gradingCompStr,
            grade = gradeStr,
            isAutograph = card.autograph,
            isPatch = card.gameUsedMaterial,
            cardType = card.cardType.name,
            collection = collectionStr,
            notes = null
        )
    }

    fun writeCardsJson(outputStream: OutputStream) {
        val dtos = exportAllCardsToJsonDtos()
        objectMapper.writeValue(outputStream, dtos)
    }

    fun exportCardsToJsonFile(file: File) {
        val dtos = exportAllCardsToJsonDtos()
        objectMapper.writeValue(file, dtos)
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

        if (card.serialNumber > 0) {
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

    private fun toSlug(input: String): String {
        val normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
        val ascii = normalized.replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
        return ascii.lowercase(Locale.ENGLISH)
            .replace(Regex("[^a-z0-9]+"), "-")
            .replace(Regex("-+"), "-")
            .trim('-')
    }
}
