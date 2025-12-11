package de.maulmann.cardcollection.model

import de.maulmann.cardcollection.repository.VariantRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class VariantTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var variantRepository: VariantRepository

    @Test
    fun `should save and retrieve variant`() {
        val variant = Variant(name = "Test Variant")
        variantRepository.save(variant)

        val foundVariant = variantRepository.findById(variant.id).orElse(null)
        assertThat(foundVariant).isEqualTo(variant)
    }
}
