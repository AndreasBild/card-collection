package de.maulmann.cardcollection.service

import de.maulmann.cardcollection.model.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CardSlugGeneratorTest {

    private fun card(
        id: Long,
        theme: String = "Base Set",
        variant: String = "Base",
        number: String = "1",
        serial: Int = 0
    ) = Card(
        id = id,
        season = Season(id = 1L, name = "1994-95"),
        number = number,
        serialNumber = serial,
        rookieCard = false,
        gameUsedMaterial = false,
        autograph = false,
        manufacturer = CardManufacturer(id = 1L, name = "Fleer"),
        brand = CardBrand(id = 1L, name = "Ultra"),
        variant = Variant(id = 1L, name = variant),
        theme = CardTheme(id = 1L, name = theme)
    )

    @Test
    fun `default theme and variant are omitted`() {
        val slug = CardSlugGenerator.generateUnique(card(1L), mutableSetOf())
        assertThat(slug).isEqualTo("1994-95-ultra-1")
    }

    @Test
    fun `non default theme variant and serial number are included`() {
        val slug = CardSlugGenerator.generateUnique(
            card(1L, theme = "Silver Prizm", variant = "Gold /10", number = "101", serial = 7),
            mutableSetOf()
        )
        assertThat(slug).isEqualTo("1994-95-ultra-silver-prizm-gold-10-101-sn7")
    }

    @Test
    fun `collisions append card id then index`() {
        val existing = mutableSetOf<String>()
        val first = CardSlugGenerator.generateUnique(card(10L), existing)
        val second = CardSlugGenerator.generateUnique(card(11L), existing)
        existing.add("$first-card11")
        val third = CardSlugGenerator.generateUnique(card(11L), existing)

        assertThat(first).isEqualTo("1994-95-ultra-1")
        assertThat(second).isEqualTo("1994-95-ultra-1-card11")
        assertThat(third).isEqualTo("1994-95-ultra-1-card11-2")
    }

    @Test
    fun `toSlug transliterates diacritics and collapses separators`() {
        assertThat(CardSlugGenerator.toSlug("  Dončić -- Ünïcode / Test  ")).isEqualTo("doncic-unicode-test")
    }
}
