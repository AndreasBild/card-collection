package de.maulmann.cardcollection.actuator

import de.maulmann.cardcollection.repository.CardRepository
import de.maulmann.cardcollection.repository.PlayerRepository
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.boot.actuate.info.Info
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class CardCollectionInfoContributorTest {

    private val cardRepository: CardRepository = mock()
    private val playerRepository: PlayerRepository = mock()

    @Test
    fun `contribute should expose card and player counts and sync path`() {
        whenever(cardRepository.count()).thenReturn(1420L)
        whenever(playerRepository.count()).thenReturn(15L)

        val contributor = CardCollectionInfoContributor(
            cardRepository = cardRepository,
            playerRepository = playerRepository,
            syncPath = "/path/to/cards.json"
        )

        val builder = Info.Builder()
        contributor.contribute(builder)
        val info = builder.build()

        @Suppress("UNCHECKED_CAST")
        val stats = info.details["cardCollection"] as? Map<String, Any>
        assertNotNull(stats)
        assertEquals(1420L, stats["totalCards"])
        assertEquals(15L, stats["totalPlayers"])
        assertEquals("/path/to/cards.json", stats["syncPath"])
    }
}
