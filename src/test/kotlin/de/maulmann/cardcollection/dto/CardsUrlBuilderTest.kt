package de.maulmann.cardcollection.dto

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CardsUrlBuilderTest {

    @Test
    fun `toQueryParams lists only active filters and skips blank print run key`() {
        val filter = CardFilter(manufacturerId = 1, rookieCard = false, printRunRangeKey = "  ", teamId = 8)

        assertThat(filter.toQueryParams()).containsExactly(
            org.assertj.core.api.Assertions.entry("manufacturerId", 1L),
            org.assertj.core.api.Assertions.entry("teamId", 8L),
            org.assertj.core.api.Assertions.entry("rookieCard", false)
        )
        assertThat(CardFilter().toQueryParams()).isEmpty()
    }

    @Test
    fun `page link carries size sort and active filters but no blank parameters`() {
        val urls = CardsUrlBuilder(CardFilter(brandId = 3, autograph = true), "10", "season.name,desc")

        assertThat(urls.page(4))
            .isEqualTo("/cards?page=4&size=10&sort=season.name,desc&brandId=3&autograph=true")
    }

    @Test
    fun `page link omits sort when none is set`() {
        assertThat(CardsUrlBuilder(CardFilter(), "all", null).page(0)).isEqualTo("/cards?page=0&size=all")
        assertThat(CardsUrlBuilder(CardFilter(), "20", "  ").page(1)).isEqualTo("/cards?page=1&size=20")
    }

    @Test
    fun `sort link resets to the first page and replaces the sort`() {
        val urls = CardsUrlBuilder(CardFilter(seasonId = 2), "50", "number,asc")

        assertThat(urls.sort("printRun,desc")).isEqualTo("/cards?page=0&size=50&sort=printRun,desc&seasonId=2")
    }

    @Test
    fun `values are percent encoded`() {
        val urls = CardsUrlBuilder(CardFilter(printRunRangeKey = "a b&c"), "10", null)

        assertThat(urls.page(0)).isEqualTo("/cards?page=0&size=10&printRunRangeKey=a%20b%26c")
    }
}
