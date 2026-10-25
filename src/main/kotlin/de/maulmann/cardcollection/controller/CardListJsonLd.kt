package de.maulmann.cardcollection.controller

import com.fasterxml.jackson.databind.ObjectMapper
import de.maulmann.cardcollection.dto.CardListItem
import de.maulmann.cardcollection.model.FeaturedPlayer

/** schema.org `CollectionPage` JSON-LD for the card list page (embedded in a `<script type="application/ld+json">`). */
object CardListJsonLd {

    private val objectMapper = ObjectMapper()

    fun build(cards: List<CardListItem>, totalItems: Long): String {
        val items = cards.mapIndexed { index, card ->
            val cardTitle = listOfNotNull(
                card.season.name,
                card.brand.name,
                card.theme.name,
                card.variant.name,
                card.playerNames,
                card.number.takeIf { it.isNotBlank() }?.let { "#$it" }
            ).filter { it.isNotBlank() }.joinToString(" ")

            mapOf(
                "@type" to "ListItem",
                "position" to (index + 1),
                "item" to mapOf(
                    "@type" to "Product",
                    "name" to cardTitle,
                    "category" to "Sports Memorabilia > Trading Cards",
                    "brand" to mapOf(
                        "@type" to "Brand",
                        "name" to card.brand.name.ifBlank { "Trading Card" }
                    ),
                    "manufacturer" to mapOf(
                        "@type" to "Organization",
                        "name" to card.manufacturer.name.ifBlank { "Manufacturer" }
                    )
                )
            )
        }

        val schemaMap = mapOf(
            "@context" to "https://schema.org",
            "@type" to "CollectionPage",
            "name" to "${FeaturedPlayer.name} Basketball Trading Card Collection",
            "description" to "Private Collection of ${FeaturedPlayer.name} Basketball Trading Cards containing rare cards from Panini, Fleer, Topps, and Upper Deck.",
            "about" to mapOf(
                "@type" to "Person",
                "name" to FeaturedPlayer.name,
                "jobTitle" to "Basketball Player"
            ),
            "mainEntity" to mapOf(
                "@type" to "ItemList",
                "numberOfItems" to totalItems,
                "itemListElement" to items
            )
        )

        return try {
            objectMapper.writeValueAsString(schemaMap)
        } catch (e: Exception) {
            "{}"
        }
    }
}
