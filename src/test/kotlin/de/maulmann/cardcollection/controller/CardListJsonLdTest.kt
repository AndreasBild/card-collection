package de.maulmann.cardcollection.controller

import com.fasterxml.jackson.databind.ObjectMapper
import de.maulmann.cardcollection.dto.CardListItem
import de.maulmann.cardcollection.model.FeaturedPlayer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

class CardListJsonLdTest {

    @AfterEach
    fun resetOwner() {
        FeaturedPlayer.name = FeaturedPlayer.DEFAULT_NAME
    }

    private fun item(brand: String = "Ultra", manufacturer: String = "Fleer", theme: String = "Base Set", number: String = "12") =
        CardListItem(
            id = 1L,
            playerNames = "Juwan Howard",
            teamNames = "",
            sportNames = "",
            season = CardListItem.NamedRef("1994-95"),
            manufacturer = CardListItem.NamedRef(manufacturer),
            brand = CardListItem.NamedRef(brand),
            theme = CardListItem.NamedRef(theme),
            variant = CardListItem.NamedRef("Base"),
            number = number,
            serialNumber = 0,
            printRun = null,
            packOdds = null,
            rookieCard = false,
            gameUsedMaterial = false,
            autograph = false,
            grading = null,
            gradingCertNumber = null,
            verificationUrl = null,
            frontScanUrl = null
        )

    @Test
    fun `builds a CollectionPage with one ListItem per card and the total count`() {
        val json = ObjectMapper().readTree(CardListJsonLd.build(listOf(item(), item(number = "")), 1471))

        assertThat(json["@type"].asText()).isEqualTo("CollectionPage")
        assertThat(json["name"].asText()).isEqualTo("Juwan Howard Basketball Trading Card Collection")
        assertThat(json["about"]["name"].asText()).isEqualTo("Juwan Howard")
        assertThat(json["mainEntity"]["numberOfItems"].asLong()).isEqualTo(1471)

        val items = json["mainEntity"]["itemListElement"]
        assertThat(items).hasSize(2)
        assertThat(items[0]["position"].asInt()).isEqualTo(1)
        assertThat(items[0]["item"]["name"].asText()).isEqualTo("1994-95 Ultra Base Set Base Juwan Howard #12")
        assertThat(items[1]["item"]["name"].asText()).isEqualTo("1994-95 Ultra Base Set Base Juwan Howard")
    }

    @Test
    fun `blank brand and manufacturer fall back to generic names`() {
        val json = ObjectMapper().readTree(CardListJsonLd.build(listOf(item(brand = "", manufacturer = "")), 1))

        val product = json["mainEntity"]["itemListElement"][0]["item"]
        assertThat(product["brand"]["name"].asText()).isEqualTo("Trading Card")
        assertThat(product["manufacturer"]["name"].asText()).isEqualTo("Manufacturer")
    }

    @Test
    fun `uses the configured owner name`() {
        FeaturedPlayer.name = "Larry Bird"

        val json = ObjectMapper().readTree(CardListJsonLd.build(emptyList(), 0))

        assertThat(json["name"].asText()).isEqualTo("Larry Bird Basketball Trading Card Collection")
        assertThat(json["about"]["name"].asText()).isEqualTo("Larry Bird")
        assertThat(json["mainEntity"]["itemListElement"]).isEmpty()
    }
}
