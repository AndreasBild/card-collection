package de.maulmann.cardcollection.repository

import de.maulmann.cardcollection.model.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class CustomCardQueriesRepositoryTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var repository: CustomCardQueriesRepository

    private lateinit var manufacturer: CardManufacturer

    @BeforeEach
    fun setUp() {
        val sport = entityManager.persist(Sport(name = "Basketball"))
        val team = entityManager.persist(Team(name = "Lakers"))
        val player = entityManager.persist(Player(name = "LeBron", surname = "James", sport = sport, team = team))
        val season = entityManager.persist(Season(name = "2022-23"))
        val variant = entityManager.persist(Variant(name = "Silver Prizm"))
        manufacturer = entityManager.persist(CardManufacturer(name = "Panini"))
        val brand = entityManager.persist(CardBrand(name = "Prizm", manufacturer = manufacturer))
        val theme = entityManager.persist(CardTheme(name = "Base Set", brand = brand))

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
    fun `findByManufacturerIdWithDetails should return cards for a specific manufacturer with details`() {
        val cards = repository.findByManufacturerIdWithDetails(manufacturer.id)
        assertEquals(1, cards.size)
    }
}
