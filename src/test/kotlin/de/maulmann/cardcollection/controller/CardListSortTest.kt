package de.maulmann.cardcollection.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.Sort

class CardListSortTest {

    @Test
    fun `no sort parameter sorts by id ascending without error`() {
        val parsed = CardListSort.parse(null)

        assertThat(parsed.sort).isEqualTo(Sort.by("id"))
        assertThat(parsed.error).isNull()
        assertThat(parsed.currentProperty).isEqualTo("id")
        assertThat(parsed.currentDirection).isEqualTo("ASC")
    }

    @Test
    fun `a sortable column with desc or descending sorts descending`() {
        assertThat(CardListSort.parse("number,desc").sort).isEqualTo(Sort.by(Sort.Direction.DESC, "number"))
        assertThat(CardListSort.parse("number,DESCENDING").sort).isEqualTo(Sort.by(Sort.Direction.DESC, "number"))
        assertThat(CardListSort.parse("season.name,asc").sort).isEqualTo(Sort.by(Sort.Direction.ASC, "season.name"))
        assertThat(CardListSort.parse("season.name").sort).isEqualTo(Sort.by(Sort.Direction.ASC, "season.name"))
    }

    @Test
    fun `an unknown or non sortable property falls back to id and reports an error`() {
        val bogus = CardListSort.parse("bogusField,desc")
        assertThat(bogus.sort).isEqualTo(Sort.by("id"))
        assertThat(bogus.error).isEqualTo("Unsupported sort property: bogusField")

        // Player names are derived, so they are listed as columns but cannot be sorted in SQL.
        assertThat(CardListSort.parse("playerNames").error).isEqualTo("Unsupported sort property: playerNames")
    }

    @Test
    fun `header direction only treats DESC as descending while the query also accepts descending`() {
        val parsed = CardListSort.parse("number,descending")

        assertThat(parsed.sort).isEqualTo(Sort.by(Sort.Direction.DESC, "number"))
        assertThat(parsed.currentDirection).isEqualTo("ASC")
        assertThat(CardListSort.parse("number,Desc").currentDirection).isEqualTo("DESC")
    }

    @Test
    fun `blank property means id and the header keeps the raw property`() {
        val parsed = CardListSort.parse(",desc")

        assertThat(parsed.sort).isEqualTo(Sort.by(Sort.Direction.DESC, "id"))
        assertThat(parsed.currentProperty).isEqualTo("id")
        assertThat(CardListSort.parse("bogus").currentProperty).isEqualTo("bogus")
    }
}
