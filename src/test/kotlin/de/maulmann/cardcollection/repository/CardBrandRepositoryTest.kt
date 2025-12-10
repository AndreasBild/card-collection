package de.maulmann.cardcollection.repository

import de.maulmann.cardcollection.model.CardBrand
import de.maulmann.cardcollection.model.CardManufacturer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class CardBrandRepositoryTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var repository: CardBrandRepository

    @Test
    fun `findAllByManufacturerIdOrderByNameAsc should return brands for a specific manufacturer`() {
        val manufacturer1 = entityManager.persistAndFlush(CardManufacturer(name = "Panini"))
        val manufacturer2 = entityManager.persistAndFlush(CardManufacturer(name = "Topps"))

        entityManager.persist(CardBrand(name = "Prizm", manufacturer = manufacturer1))
        entityManager.persist(CardBrand(name = "Select", manufacturer = manufacturer1))
        entityManager.persist(CardBrand(name = "Chrome", manufacturer = manufacturer2))

        val brands = repository.findAllByManufacturerIdOrderByNameAsc(manufacturer1.id)

        assertEquals(2, brands.size)
        assertEquals("Prizm", brands[0].name)
        assertEquals("Select", brands[1].name)
    }

    @Test
    fun `findAllByOrderByNameAsc should return all brands sorted by name`() {
        val manufacturer = entityManager.persistAndFlush(CardManufacturer(name = "Upper Deck"))
        entityManager.persist(CardBrand(name = "Series 1", manufacturer = manufacturer))
        entityManager.persist(CardBrand(name = "Artifacts", manufacturer = manufacturer))
        entityManager.persist(CardBrand(name = "SP Authentic", manufacturer = manufacturer))

        val brands = repository.findAllByOrderByNameAsc()

        assertEquals(3, brands.size)
        assertEquals("Artifacts", brands[0].name)
        assertEquals("SP Authentic", brands[1].name)
        assertEquals("Series 1", brands[2].name)
    }
}
