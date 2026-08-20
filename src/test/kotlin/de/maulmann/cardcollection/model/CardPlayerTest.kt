package de.maulmann.cardcollection.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class CardPlayerTest {

    @Test
    fun `test CardPlayer equals and hashCode`() {
        val season = Season(id = 1, name = "1994-95")
        val mfg = CardManufacturer(id = 1, name = "Topps")
        val brand = CardBrand(id = 1, name = "Finest")
        val theme = CardTheme(id = 1, name = "Base Set")
        val variant = Variant(id = 1, name = "Base")
        val card = Card(
            id = 1,
            serialNumber = 1,
            season = season,
            number = "1",
            rookieCard = false,
            gameUsedMaterial = false,
            autograph = false,
            manufacturer = mfg,
            brand = brand,
            theme = theme,
            variant = variant
        )
        val sport = Sport(id = 1, name = "Basketball")
        val player = Player(id = 1, name = "Juwan", surname = "Howard", sport = sport)
        val team = Team(id = 1, name = "Bullets")

        val cp1 = CardPlayer(id = CardPlayerId(cardId = 1, playerId = 1), card = card, player = player, team = team)
        val cp2 = CardPlayer(id = CardPlayerId(cardId = 1, playerId = 1), card = card, player = player, team = team)
        val cp3 = CardPlayer(id = CardPlayerId(cardId = 1, playerId = 2), card = card, player = player, team = team)

        assertEquals(cp1, cp2)
        assertEquals(cp1.hashCode(), cp2.hashCode())
        assertNotEquals(cp1, cp3)
        assertNotEquals(cp1, "other type")
    }
}
