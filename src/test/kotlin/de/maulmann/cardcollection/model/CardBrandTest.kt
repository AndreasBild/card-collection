package de.maulmann.cardcollection.model

import de.maulmann.cardcollection.repository.CardBrandRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager

@DataJpaTest
class CardBrandTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var cardBrandRepository: CardBrandRepository

    @Test
    fun `should save and retrieve card brand`() {
        val cardBrand = CardBrand(name = "Test Brand")
        cardBrandRepository.save(cardBrand)

        val foundCardBrand = cardBrandRepository.findById(cardBrand.id).orElse(null)
        assertThat(foundCardBrand).isEqualTo(cardBrand)
    }
}
