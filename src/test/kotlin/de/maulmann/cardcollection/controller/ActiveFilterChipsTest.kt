package de.maulmann.cardcollection.controller

import de.maulmann.cardcollection.dto.CardFilter
import de.maulmann.cardcollection.model.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ActiveFilterChipsTest {

    private val sport = Sport(id = 1L, name = "Basketball")
    private val lookups = FilterLookups(
        manufacturers = listOf(CardManufacturer(id = 5L, name = "Upper Deck")),
        brands = listOf(CardBrand(id = 2L, name = "SP")),
        themes = emptyList(),
        sports = listOf(sport),
        players = listOf(Player(id = 7L, name = "Juwan", surname = "Howard", sport = sport)),
        teams = emptyList(),
        seasons = emptyList(),
        variants = emptyList()
    )

    @Test
    fun `no filters means no chips`() {
        assertThat(ActiveFilterChips.build(CardFilter(), lookups, "20", null)).isEmpty()
    }

    @Test
    fun `ids are resolved to names and unknown ids fall back to the id`() {
        val chips = ActiveFilterChips.build(
            CardFilter(manufacturerId = 5, brandId = 2, playerId = 7, teamId = 99), lookups, "20", null
        )

        assertThat(chips.map { it.label to it.value }).containsExactly(
            "Manufacturer" to "Upper Deck",
            "Brand" to "SP",
            "Player" to "Juwan Howard",
            "Team" to "99"
        )
    }

    @Test
    fun `boolean and print run filters render as Yes No and the range name`() {
        val chips = ActiveFilterChips.build(
            CardFilter(gameUsed = true, autograph = false, rookieCard = true, isGradedNullable = false, printRunRangeKey = "nope"),
            lookups, "20", null
        )

        assertThat(chips.associate { it.label to it.value }).containsExactlyInAnyOrderEntriesOf(
            mapOf(
                "Game Used" to "Yes",
                "Autograph" to "No",
                "Rookie" to "Yes",
                "Graded" to "No",
                "Print Run" to "nope"
            )
        )
    }

    @Test
    fun `each chip links to the same list without its own filter and keeps non default size and sort`() {
        val chips = ActiveFilterChips.build(CardFilter(manufacturerId = 5, brandId = 2), lookups, "50", "number,desc")

        assertThat(chips.first { it.label == "Manufacturer" }.removeUrl)
            .isEqualTo("/cards?size=50&sort=number,desc&brandId=2")
        assertThat(chips.first { it.label == "Brand" }.removeUrl)
            .isEqualTo("/cards?size=50&sort=number,desc&manufacturerId=5")
    }

    @Test
    fun `default size is omitted from remove links`() {
        val chips = ActiveFilterChips.build(CardFilter(brandId = 2), lookups, "20", null)

        assertThat(chips.single().removeUrl).isEqualTo("/cards")
    }
}
