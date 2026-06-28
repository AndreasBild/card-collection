package de.maulmann.cardcollection.repository

import de.maulmann.cardcollection.model.Variant
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager

@DataJpaTest
class VariantRepositoryTest @Autowired constructor(
    val entityManager: TestEntityManager,
    val variantRepository: VariantRepository
) {

    @Test
    fun `when findById then return Variant`() {
        val variant = Variant(name = "Gold")
        entityManager.persistAndFlush(variant)
        val found = variantRepository.findById(variant.id)
        assertThat(found.get()).isEqualTo(variant)
    }
}
