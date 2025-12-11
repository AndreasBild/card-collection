package de.maulmann.cardcollection.model

import de.maulmann.cardcollection.repository.CardThemeRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager

@DataJpaTest
class CardThemeTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var cardThemeRepository: CardThemeRepository

    @Test
    fun `should save and retrieve card theme`() {
        val manufacturer = CardManufacturer(name = "Test Manufacturer")
        entityManager.persist(manufacturer)

        val brand = CardBrand(name = "Test Brand", manufacturer = manufacturer)
        entityManager.persist(brand)

        val cardTheme = CardTheme(name = "Test Theme", brand = brand)
        cardThemeRepository.save(cardTheme)

        val foundCardTheme = cardThemeRepository.findById(cardTheme.id).orElse(null)
        assertThat(foundCardTheme).isEqualTo(cardTheme)
    }
}
