package de.maulmann.cardcollection.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CardPeopleSummaryTest {

    private fun cardWithPlayers(vararg entries: Triple<Player, Team?, Long>): Card {
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
        for ((player, team, id) in entries) {
            card.cardPlayers.add(CardPlayer(CardPlayerId(card.id, id), card, player, team))
        }
        return card
    }

    @Test
    fun `peopleSummary matches individual getters and puts Juwan Howard first`() {
        val basketball = Sport(id = 1L, name = "Basketball")
        val bullets = Team(id = 1L, name = "Washington Bullets")
        val lakers = Team(id = 2L, name = "Lakers")
        val shaq = Player(id = 1L, name = "Shaquille", surname = "O'Neal", sport = basketball)
        val juwan = Player(id = 2L, name = "Juwan", surname = "Howard", sport = basketball)

        val card = cardWithPlayers(Triple(shaq, lakers, 1L), Triple(juwan, bullets, 2L))

        val summary = card.peopleSummary()

        assertThat(summary.playerNames).isEqualTo("Juwan Howard, Shaquille O'Neal")
        assertThat(summary.sportNames).isEqualTo("Basketball")
        assertThat(summary.teamNames).isEqualTo("Washington Bullets, Lakers")
        assertThat(summary.playerNames).isEqualTo(card.playerNames)
        assertThat(summary.sportNames).isEqualTo(card.sportNames)
        assertThat(summary.teamNames).isEqualTo(card.teamNames)
    }

    @Test
    fun `other players are ordered by surname name and id regardless of insertion order`() {
        val basketball = Sport(id = 1L, name = "Basketball")
        val zed = Player(id = 3L, name = "Zed", surname = "Adams", sport = basketball)
        val amy = Player(id = 1L, name = "Amy", surname = "Brown", sport = basketball)
        val ann = Player(id = 2L, name = "Ann", surname = "Brown", sport = basketball)
        val juwan = Player(id = 4L, name = "Juwan", surname = "Howard", sport = basketball)

        val forward = cardWithPlayers(Triple(ann, null, 2L), Triple(juwan, null, 4L), Triple(amy, null, 1L), Triple(zed, null, 3L))
        val reversed = cardWithPlayers(Triple(zed, null, 3L), Triple(amy, null, 1L), Triple(juwan, null, 4L), Triple(ann, null, 2L))

        val expected = "Juwan Howard, Zed Adams, Amy Brown, Ann Brown"
        assertThat(forward.peopleSummary().playerNames).isEqualTo(expected)
        assertThat(reversed.peopleSummary().playerNames).isEqualTo(expected)
    }

    @Test
    fun `peopleSummary is empty for card without players`() {
        val summary = cardWithPlayers().peopleSummary()

        assertThat(summary.playerNames).isEmpty()
        assertThat(summary.sportNames).isEmpty()
        assertThat(summary.teamNames).isEmpty()
    }
}
