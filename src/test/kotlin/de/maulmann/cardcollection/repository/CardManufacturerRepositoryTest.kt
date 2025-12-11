package de.maulmann.cardcollection.repository

import de.maulmann.cardcollection.model.CardManufacturer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class CardManufacturerRepositoryTest @Autowired constructor(
    val entityManager: TestEntityManager,
    val cardManufacturerRepository: CardManufacturerRepository
) {

    @Test
    fun `when findById then return CardManufacturer`() {
        val cardManufacturer = CardManufacturer(name = "Panini")
        entityManager.persistAndFlush(cardManufacturer)
        val found = cardManufacturerRepository.findById(cardManufacturer.id!!)
        assertThat(found.get()).isEqualTo(cardManufacturer)
    }
}
