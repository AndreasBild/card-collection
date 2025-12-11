package de.maulmann.cardcollection.model

import de.maulmann.cardcollection.repository.CardManufacturerRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class CardManufacturerTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var cardManufacturerRepository: CardManufacturerRepository

    @Test
    fun `should save and retrieve card manufacturer`() {
        val cardManufacturer = CardManufacturer(name = "Test Manufacturer")
        cardManufacturerRepository.save(cardManufacturer)

        val foundCardManufacturer = cardManufacturerRepository.findById(cardManufacturer.id).orElse(null)
        assertThat(foundCardManufacturer).isEqualTo(cardManufacturer)
    }
}
