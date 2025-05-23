package de.maulmann.cardcollection.repository

import de.maulmann.cardcollection.model.CardBrand
import de.maulmann.cardcollection.model.CardManufacturer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class CardBrandRepositoryTest {

    @Autowired
    lateinit var entityManager: TestEntityManager

    @Autowired
    lateinit var cardBrandRepository: CardBrandRepository

    @Test
    fun `should find all brands by manufacturer id ordered by name ascending`() {
        // Sport is not a direct property of CardManufacturer
        val manufacturer1 = CardManufacturer(name = "Panini")
        val manufacturer2 = CardManufacturer(name = "Topps")
        entityManager.persist(manufacturer1)
        entityManager.persist(manufacturer2)
        entityManager.flush() // Ensure IDs are generated

        val brandC = CardBrand(name = "Prizm C", manufacturer = manufacturer1)
        val brandA = CardBrand(name = "Donruss A", manufacturer = manufacturer1)
        val brandB = CardBrand(name = "Chrome B", manufacturer = manufacturer2) // Belongs to manufacturer2
        entityManager.persist(brandC)
        entityManager.persist(brandA)
        entityManager.persist(brandB)
        entityManager.flush()

        val brands = cardBrandRepository.findAllByManufacturerIdOrderByNameAsc(manufacturer1.id!!)
        
        assertThat(brands).hasSize(2)
        val extractedNames: List<String> = brands.map { it.name }
        assertThat(extractedNames).containsExactlyElementsOf(listOf("Donruss A", "Prizm C"))
        assertThat(brands).allSatisfy { brand ->
            assertThat(brand.manufacturer.id).isEqualTo(manufacturer1.id)
        }
    }

    @Test
    fun `should return empty list when no brands match manufacturer id`() {
        // Sport is not a direct property of CardManufacturer
        val manufacturer1 = CardManufacturer(name = "Panini")
        val manufacturer2 = CardManufacturer(name = "Topps") // Exists but no brands linked for this test
        entityManager.persist(manufacturer1)
        entityManager.persist(manufacturer2)
        entityManager.flush()

        val brandA = CardBrand(name = "Donruss A", manufacturer = manufacturer1)
        entityManager.persist(brandA)
        entityManager.flush()

        val brands = cardBrandRepository.findAllByManufacturerIdOrderByNameAsc(manufacturer2.id!!)
        assertThat(brands).isEmpty()
    }

    @Test
    fun `should find all brands ordered by name ascending`() {
        // Sport is not a direct property of CardManufacturer
        val manufacturer1 = CardManufacturer(name = "Panini")
        val manufacturer2 = CardManufacturer(name = "Topps")
        entityManager.persist(manufacturer1)
        entityManager.persist(manufacturer2)
        entityManager.flush()

        // Brands linked to different manufacturers to ensure ordering is global
        val brandZ = CardBrand(name = "Zenith Z", manufacturer = manufacturer1)
        val brandX = CardBrand(name = "Select X", manufacturer = manufacturer2)
        val brandA = CardBrand(name = "Alpha A", manufacturer = manufacturer1)
        val brandM = CardBrand(name = "Museum M", manufacturer = manufacturer2)

        entityManager.persist(brandZ) // Panini
        entityManager.persist(brandX) // Topps
        entityManager.persist(brandA) // Panini
        entityManager.persist(brandM) // Topps
        entityManager.flush()

        val brands = cardBrandRepository.findAllByOrderByNameAsc()
        
        assertThat(brands).hasSize(4)
        val extractedNamesGlobal: List<String> = brands.map { it.name }
        assertThat(extractedNamesGlobal).containsExactlyElementsOf(listOf("Alpha A", "Museum M", "Select X", "Zenith Z"))
    }

    @Test
    fun `should return empty list when no brands exist for findAllByOrderByNameAsc`() {
        // No manufacturers or brands persisted
        val brands = cardBrandRepository.findAllByOrderByNameAsc()
        assertThat(brands).isEmpty()
    }
}
