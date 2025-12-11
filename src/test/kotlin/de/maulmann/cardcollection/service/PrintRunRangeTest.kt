package de.maulmann.cardcollection.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class PrintRunRangeTest {

    @ParameterizedTest
    @CsvSource(
        "ONE, 1, true, false",
        "LE_10, 10, false, false",
        "LE_50, 50, false, false",
        "LE_100, 100, false, false",
        "LE_500, 500, false, false",
        "LE_1000, 1000, false, false",
        "ALL_WITH_PRINT_RUN, , false, true"
    )
    fun `should have correct properties`(
        key: String,
        expectedValue: Int?,
        isExact: Boolean,
        isAllWithPrintRun: Boolean
    ) {
        val range = PrintRunRange.fromKey(key)
        assertThat(range).isNotNull
        if (isExact) {
            assertThat(range?.exactValue).isEqualTo(expectedValue)
            assertThat(range?.maxValue).isNull()
        } else if (!isAllWithPrintRun) {
            assertThat(range?.maxValue).isEqualTo(expectedValue)
            assertThat(range?.exactValue).isNull()
        }
        assertThat(range?.isAllWithPrintRun).isEqualTo(isAllWithPrintRun)
    }

    @Test
    fun `fromKey should return correct enum for valid key`() {
        assertThat(PrintRunRange.fromKey("ONE")).isEqualTo(PrintRunRange.ONE)
        assertThat(PrintRunRange.fromKey("LE_100")).isEqualTo(PrintRunRange.LE_100)
    }

    @Test
    fun `fromKey should return null for invalid key`() {
        assertThat(PrintRunRange.fromKey("INVALID_KEY")).isNull()
    }

    @Test
    fun `fromKey should return null for null key`() {
        assertThat(PrintRunRange.fromKey(null)).isNull()
    }
}
