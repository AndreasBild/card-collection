package de.maulmann.cardcollection.model

import de.maulmann.cardcollection.repository.CardRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class CardTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var cardRepository: CardRepository

    @Test
    fun `should save and retrieve card`() {
        val manufacturer = CardManufacturer(name = "Test Manufacturer")
        entityManager.persist(manufacturer)

        val brand = CardBrand(name = "Test Brand", manufacturer = manufacturer)
        entityManager.persist(brand)

        val theme = CardTheme(name = "Test Theme", brand = brand)
        entityManager.persist(theme)

        val sport = Sport(name = "Test Sport")
        entityManager.persist(sport)

        val team = Team(name = "Test Team")
        entityManager.persist(team)

        val player = Player(name = "Test", surname = "Player", team = team, sport = sport)
        entityManager.persist(player)

        val season = Season(name = "2023-24")
        entityManager.persist(season)

        val variant = Variant(name = "Test Variant")
        entityManager.persist(variant)

        val grading = Grading(grade = 9.5f, gradingCompany = GradingCompany.PSA)
        entityManager.persist(grading)

        val card = Card(
            grading = grading,
            printRun = 100,
            serialNumber = 1,
            season = season,
            number = "123",
            rookieCard = true,
            gameUsedMaterial = false,
            autograph = true,
            player = player,
            variant = variant,
            theme = theme
        )
        cardRepository.save(card)

        val foundCard = cardRepository.findById(card.id).orElse(null)
        assertThat(foundCard).isEqualTo(card)
    }
}
