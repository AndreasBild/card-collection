package de.maulmann.cardcollection.repository

import de.maulmann.cardcollection.model.CardBrand
import de.maulmann.cardcollection.model.CardManufacturer
import de.maulmann.cardcollection.model.CardTheme
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class CardThemeRepositoryTest {

    @Autowired
    lateinit var entityManager: TestEntityManager

    @Autowired
    lateinit var cardThemeRepository: CardThemeRepository

    // Helper to create common entities
    private fun setupManufacturerAndBrand(mName: String, bName: String): CardBrand {
        // Sport is not a direct property of CardManufacturer or Team
        val manufacturer = CardManufacturer(name = mName)
        entityManager.persist(manufacturer)
        entityManager.flush() // Ensure manufacturer ID is generated
        val brand = CardBrand(name = bName, manufacturer = manufacturer)
        entityManager.persist(brand)
        entityManager.flush() // Ensure brand ID is generated
        return brand
    }

    @Test
    fun `should find all themes by brand id ordered by name ascending`() {
        val brand1 = setupManufacturerAndBrand("Panini", "Prizm")
        val brand2 = setupManufacturerAndBrand("Panini", "Select") // Same manufacturer, different brand

        val themeC = CardTheme(name = "Prizm Theme C", brand = brand1)
        val themeA = CardTheme(name = "Prizm Theme A", brand = brand1)
        val themeB_brand2 = CardTheme(name = "Select Theme B", brand = brand2) // Belongs to brand2
        entityManager.persist(themeC)
        entityManager.persist(themeA)
        entityManager.persist(themeB_brand2)
        entityManager.flush()

        val themes = cardThemeRepository.findAllByBrandIdOrderByNameAsc(brand1.id!!)
        
        assertThat(themes).hasSize(2)
        val extractedNames: List<String> = themes.map { it.name }
        assertThat(extractedNames).containsExactlyElementsOf(listOf("Prizm Theme A", "Prizm Theme C"))
        assertThat(themes).allSatisfy { theme ->
            assertThat(theme.brand!!.id).isEqualTo(brand1.id)
        }
    }

    @Test
    fun `should return empty list when no themes match brand id`() {
        val brand1 = setupManufacturerAndBrand("Panini", "Prizm")
        val brandWithNoThemes = setupManufacturerAndBrand("Panini", "EmptyBrand")

        val themeA = CardTheme(name = "Prizm Theme A", brand = brand1)
        entityManager.persist(themeA)
        entityManager.flush()

        val themes = cardThemeRepository.findAllByBrandIdOrderByNameAsc(brandWithNoThemes.id!!)
        assertThat(themes).isEmpty()
    }


    @Test
    fun `should find all themes by brand manufacturer id ordered by name ascending`() {
        // Sport instances are not directly related to CardManufacturer
        val manufacturer1 = CardManufacturer(name = "Panini M1")
        entityManager.persist(manufacturer1)
        entityManager.flush()
        val manufacturer2 = CardManufacturer(name = "Topps M2") // Different manufacturer
        entityManager.persist(manufacturer2)
        entityManager.flush()

        val brand1M1 = CardBrand(name = "Brand B1M1", manufacturer = manufacturer1)
        entityManager.persist(brand1M1)
        entityManager.flush()
        val brand2M1 = CardBrand(name = "Brand A2M1", manufacturer = manufacturer1) // Name starts with A for ordering test
        entityManager.persist(brand2M1)
        entityManager.flush()
        val brand1M2 = CardBrand(name = "Brand C1M2", manufacturer = manufacturer2)
        entityManager.persist(brand1M2)
        entityManager.flush()

        // Themes for Manufacturer1
        val themeZA2M1 = CardTheme(name = "Theme Z for A2M1", brand = brand2M1) // Belongs to brand A2M1 (manu M1)
        val themeXB1M1 = CardTheme(name = "Theme X for B1M1", brand = brand1M1) // Belongs to brand B1M1 (manu M1)
        // Theme for Manufacturer2
        val themeYC1M2 = CardTheme(name = "Theme Y for C1M2", brand = brand1M2)
        
        entityManager.persist(themeZA2M1)
        entityManager.persist(themeXB1M1)
        entityManager.persist(themeYC1M2)
        entityManager.flush()

        val themes = cardThemeRepository.findAllByBrandManufacturerIdOrderByNameAsc(manufacturer1.id!!)
        
        assertThat(themes).hasSize(2)
        // Order should be by theme name, across brands of the same manufacturer
        val extractedNamesForManu: List<String> = themes.map { it.name }
        assertThat(extractedNamesForManu).containsExactlyElementsOf(listOf("Theme X for B1M1", "Theme Z for A2M1"))
        assertThat(themes).allSatisfy { theme ->
            assertThat(theme.brand!!.manufacturer.id).isEqualTo(manufacturer1.id)
        }
    }

    @Test
    fun `should return empty list when no themes match manufacturer id for findAllByBrandManufacturerId`() {
        // Sport instances are not directly related to CardManufacturer
        val manufacturer1 = CardManufacturer(name = "Panini M1")
        entityManager.persist(manufacturer1)
        val manufacturerWithNoThemes = CardManufacturer(name = "EmptyManufacturer")
        entityManager.persist(manufacturerWithNoThemes)
        entityManager.flush()
        
        val brand1M1 = CardBrand(name = "Brand B1M1", manufacturer = manufacturer1)
        entityManager.persist(brand1M1)
        entityManager.flush()
        val themeXB1M1 = CardTheme(name = "Theme X for B1M1", brand = brand1M1)
        entityManager.persist(themeXB1M1)
        entityManager.flush()

        val themes = cardThemeRepository.findAllByBrandManufacturerIdOrderByNameAsc(manufacturerWithNoThemes.id!!)
        assertThat(themes).isEmpty()
    }

    @Test
    fun `should find all themes ordered by name ascending`() {
        val brand1 = setupManufacturerAndBrand("Panini", "Prizm")
        val brand2 = setupManufacturerAndBrand("Topps", "Chrome")

        val themeZ = CardTheme(name = "Theme Z", brand = brand1)
        val themeX = CardTheme(name = "Theme X", brand = brand2)
        val themeY = CardTheme(name = "Theme Y", brand = brand1)
        entityManager.persist(themeZ)
        entityManager.persist(themeX)
        entityManager.persist(themeY)
        entityManager.flush()

        val themes = cardThemeRepository.findAllByOrderByNameAsc()
        
        assertThat(themes).hasSize(3)
        val extractedNamesGlobal: List<String> = themes.map { it.name }
        assertThat(extractedNamesGlobal).containsExactlyElementsOf(listOf("Theme X", "Theme Y", "Theme Z"))
    }

    @Test
    fun `should return empty list when no themes exist for findAllByOrderByNameAsc`() {
        // No manufacturers, brands, or themes persisted
        val themes = cardThemeRepository.findAllByOrderByNameAsc()
        assertThat(themes).isEmpty()
    }
}
