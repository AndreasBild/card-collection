package de.maulmann.cardcollection.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class GradingCompanyTest {

    @Test
    fun `should have correct enum values`() {
        val values = GradingCompany.entries.toTypedArray()
        assertThat(values).containsExactly(GradingCompany.PSA, GradingCompany.BGS, GradingCompany.MBA)
    }
}
