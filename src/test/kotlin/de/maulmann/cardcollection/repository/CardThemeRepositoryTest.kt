package de.maulmann.cardcollection.repository

import de.maulmann.cardcollection.model.CardBrand
import de.maulmann.cardcollection.model.CardManufacturer
import de.maulmann.cardcollection.model.CardTheme
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class CardThemeRepositoryTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var repository: CardThemeRepository

    private lateinit var brand1: CardBrand
    private lateinit var brand2: CardBrand

    @BeforeEach
    fun setUp() {
        val manufacturer1 = entityManager.persist(CardManufacturer(name = "Panini"))
        val manufacturer2 = entityManager.persist(CardManufacturer(name = "Topps"))
        brand1 = entityManager.persist(CardBrand(name = "Prizm", manufacturer = manufacturer1))
        brand2 = entityManager.persist(CardBrand(name = "Chrome", manufacturer = manufacturer2))

        entityManager.persist(CardTheme(name = "Base Set", brand = brand1))
        entityManager.persist(CardTheme(name = "Rookie Variations", brand = brand1))
        entityManager.persist(CardTheme(name = "Refractors", brand = brand2))
    }

    @Test
    fun `findAllByBrandIdOrderByNameAsc should return themes for a specific brand`() {
        val themes = repository.findAllByBrandIdOrderByNameAsc(brand1.id)
        assertEquals(2, themes.size)
        assertEquals("Base Set", themes[0].name)
        assertEquals("Rookie Variations", themes[1].name)
    }

    @Test
    fun `findAllByBrandManufacturerIdOrderByNameAsc should return themes for a specific manufacturer`() {
        val themes = repository.findAllByBrandManufacturerIdOrderByNameAsc(brand1.manufacturer.id)
        assertEquals(2, themes.size)
    }

    @Test
    fun `findAllByOrderByNameAsc should return all themes sorted by name`() {
        val themes = repository.findAllByOrderByNameAsc()
        assertEquals(3, themes.size)
        assertEquals("Base Set", themes[0].name)
        assertEquals("Refractors", themes[1].name)
        assertEquals("Rookie Variations", themes[2].name)
    }
}
