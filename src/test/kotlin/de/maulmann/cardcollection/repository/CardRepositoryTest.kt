package de.maulmann.cardcollection.repository

import de.maulmann.cardcollection.model.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class CardRepositoryTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var repository: CardRepository

    private lateinit var player: Player
    private lateinit var season: Season
    private lateinit var variant: Variant
    private lateinit var theme: CardTheme

    @BeforeEach
    fun setUp() {
        val sport = entityManager.persist(Sport(name = "Basketball"))
        val team = entityManager.persist(Team(name = "Lakers"))
        player = entityManager.persist(Player(name = "LeBron", surname = "James", sport = sport, team = team))
        season = entityManager.persist(Season(name = "2022-23"))
        variant = entityManager.persist(Variant(name = "Silver Prizm"))
        val manufacturer = entityManager.persist(CardManufacturer(name = "Panini"))
        val brand = entityManager.persist(CardBrand(name = "Prizm", manufacturer = manufacturer))
        theme = entityManager.persist(CardTheme(name = "Base Set", brand = brand))

        entityManager.persist(
            Card(
                number = "23",
                autograph = true,
                gameUsedMaterial = false,
                rookieCard = false,
                printRun = 100,
                serialNumber = 1,
                player = player,
                season = season,
                variant = variant,
                theme = theme
            )
        )
    }

    @Test
    fun `findAllWithDetails should return cards with all relations fetched`() {
        val cards = repository.findAllWithDetails()
        assertEquals(1, cards.size)
    }

    @Test
    fun `findAllByRookieCard should return rookie cards`() {
        entityManager.persist(
            Card(
                number = "1",
                rookieCard = true,
                player = player,
                season = season,
                variant = variant,
                theme = theme,
                autograph = false,
                gameUsedMaterial = false,
                printRun = 100,
                serialNumber = 1
            )
        )
        val rookieCards = repository.findAllByRookieCard(true)
        assertEquals(1, rookieCards.size)
        assertTrue(rookieCards[0].rookieCard)
    }
}
