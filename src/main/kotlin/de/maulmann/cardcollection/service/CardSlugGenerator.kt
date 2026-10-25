package de.maulmann.cardcollection.service

import de.maulmann.cardcollection.model.Card
import java.text.Normalizer
import java.util.Locale

/**
 * Deterministic slug rules for exported cards: `[season]-[brand]-[theme]-[variant]-[number]-sn[serial]`, omitting the
 * default theme ("Base Set") and variant ("Base"). Collisions get `-card<id>`, then `-card<id>-<index>`.
 */
object CardSlugGenerator {

    private val DIACRITICS_REGEX = Regex("\\p{InCombiningDiacriticalMarks}+")
    private val NON_ALPHANUMERIC_REGEX = Regex("[^a-z0-9]+")
    private val CONSECUTIVE_DASHES_REGEX = Regex("-+")

    fun generateUnique(card: Card, existingSlugs: MutableSet<String>): String {
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

    fun toSlug(input: String): String {
        val normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
        val ascii = DIACRITICS_REGEX.replace(normalized, "")
        return NON_ALPHANUMERIC_REGEX.replace(ascii.lowercase(Locale.ENGLISH), "-")
            .replace(CONSECUTIVE_DASHES_REGEX, "-")
            .trim('-')
    }
}
