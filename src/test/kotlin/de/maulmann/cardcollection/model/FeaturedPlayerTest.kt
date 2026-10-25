package de.maulmann.cardcollection.model

import de.maulmann.cardcollection.config.CollectionConfig
import de.maulmann.cardcollection.config.CollectionProperties
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

class FeaturedPlayerTest {

    @AfterEach
    fun resetOwner() {
        FeaturedPlayer.name = FeaturedPlayer.DEFAULT_NAME
    }

    private fun cardWithPlayers(vararg players: Player): Card {
        val card = Card(
            id = 1L,
            season = Season(id = 1L, name = "1994-95"),
            number = "1",
            serialNumber = 0,
            rookieCard = false,
            gameUsedMaterial = false,
            autograph = false,
            manufacturer = CardManufacturer(id = 1L, name = "Fleer"),
            brand = CardBrand(id = 1L, name = "Ultra"),
            variant = Variant(id = 1L, name = "Base"),
            theme = CardTheme(id = 1L, name = "Base Set")
        )
        players.forEachIndexed { index, player ->
            card.cardPlayers.add(CardPlayer(CardPlayerId(card.id, index + 1L), card, player, null))
        }
        return card
    }

    @Test
    fun `default owner is Juwan Howard`() {
        assertThat(FeaturedPlayer.name).isEqualTo("Juwan Howard")
    }

    @Test
    fun `configured owner is listed first on multi player cards`() {
        val sport = Sport(id = 1L, name = "Basketball")
        val adams = Player(id = 1L, name = "Zed", surname = "Adams", sport = sport)
        val owner = Player(id = 2L, name = "Larry", surname = "Bird", sport = sport)
        val card = cardWithPlayers(adams, owner)

        assertThat(card.peopleSummary().playerNames).isEqualTo("Zed Adams, Larry Bird")

        CollectionConfig(CollectionProperties(ownerName = "Larry Bird")).publishOwnerName()

        assertThat(card.peopleSummary().playerNames).isEqualTo("Larry Bird, Zed Adams")
    }

    @Test
    fun `blank configured owner falls back to the default`() {
        CollectionConfig(CollectionProperties(ownerName = "   ")).publishOwnerName()

        assertThat(FeaturedPlayer.name).isEqualTo(FeaturedPlayer.DEFAULT_NAME)
    }
}
