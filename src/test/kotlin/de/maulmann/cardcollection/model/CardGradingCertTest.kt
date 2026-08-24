package de.maulmann.cardcollection.model

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CardGradingCertTest {

    private val season = Season(id = 1L, name = "1997-98")
    private val manufacturer = CardManufacturer(id = 1L, name = "Fleer")
    private val brand = CardBrand(id = 1L, name = "Fleer Metal Universe")
    private val theme = CardTheme(id = 1L, name = "Base Set")
    private val variant = Variant(id = 1L, name = "Base")

    @Test
    fun `test psa verification url generated correctly`() {
        val grading = Grading(id = 1L, grade = 10.0f, gradingCompany = GradingCompany.PSA)
        val card = Card(
            id = 1L,
            season = season,
            manufacturer = manufacturer,
            brand = brand,
            variant = variant,
            theme = theme,
            number = "33",
            serialNumber = 0,
            rookieCard = false,
            gameUsedMaterial = false,
            autograph = false,
            grading = grading,
            gradingCertNumber = "83921049"
        )

        assertEquals("https://www.psacard.com/cert/83921049", card.verificationUrl)
    }

    @Test
    fun `test bgs verification url generated correctly with trimming`() {
        val grading = Grading(id = 1L, grade = 9.5f, gradingCompany = GradingCompany.BGS)
        val card = Card(
            id = 2L,
            season = season,
            manufacturer = manufacturer,
            brand = brand,
            variant = variant,
            theme = theme,
            number = "33",
            serialNumber = 0,
            rookieCard = false,
            gameUsedMaterial = false,
            autograph = false,
            grading = grading,
            gradingCertNumber = "  0012345678  "
        )

        assertEquals("https://www.beckett.com/grading/card-lookup?item_type=BGS&item_id=0012345678", card.verificationUrl)
    }

    @Test
    fun `test mba verification url generated correctly`() {
        val grading = Grading(id = 1L, grade = 9.0f, gradingCompany = GradingCompany.MBA)
        val card = Card(
            id = 3L,
            season = season,
            manufacturer = manufacturer,
            brand = brand,
            variant = variant,
            theme = theme,
            number = "33",
            serialNumber = 0,
            rookieCard = false,
            gameUsedMaterial = false,
            autograph = false,
            grading = grading,
            gradingCertNumber = "MBA-99882"
        )

        assertEquals("https://www.mbagrading.com/cert/MBA-99882", card.verificationUrl)
    }

    @Test
    fun `test verification url is null when cert number is null or blank`() {
        val grading = Grading(id = 1L, grade = 10.0f, gradingCompany = GradingCompany.PSA)
        val cardNull = Card(
            id = 4L,
            season = season,
            manufacturer = manufacturer,
            brand = brand,
            variant = variant,
            theme = theme,
            number = "33",
            serialNumber = 0,
            rookieCard = false,
            gameUsedMaterial = false,
            autograph = false,
            grading = grading,
            gradingCertNumber = null
        )
        assertNull(cardNull.verificationUrl)

        val cardBlank = Card(
            id = 5L,
            season = season,
            manufacturer = manufacturer,
            brand = brand,
            variant = variant,
            theme = theme,
            number = "33",
            serialNumber = 0,
            rookieCard = false,
            gameUsedMaterial = false,
            autograph = false,
            grading = grading,
            gradingCertNumber = "   "
        )
        assertNull(cardBlank.verificationUrl)
    }

    @Test
    fun `test verification url is null when card is not graded`() {
        val card = Card(
            id = 6L,
            season = season,
            manufacturer = manufacturer,
            brand = brand,
            variant = variant,
            theme = theme,
            number = "33",
            serialNumber = 0,
            rookieCard = false,
            gameUsedMaterial = false,
            autograph = false,
            grading = null,
            gradingCertNumber = "83921049"
        )
        assertNull(card.verificationUrl)
    }
}
