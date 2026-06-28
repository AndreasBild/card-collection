package de.maulmann.cardcollection.repository

import de.maulmann.cardcollection.model.CardTheme
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager

@DataJpaTest
class CardThemeRepositoryTest @Autowired constructor(
    val entityManager: TestEntityManager,
    val cardThemeRepository: CardThemeRepository
) {

    @Test
    fun `when findById then return CardTheme`() {
        val cardTheme = CardTheme(name = "Test Theme", brand = null)
        entityManager.persistAndFlush(cardTheme)
        val found = cardThemeRepository.findById(cardTheme.id)
        assertThat(found.get()).isEqualTo(cardTheme)
    }
}
