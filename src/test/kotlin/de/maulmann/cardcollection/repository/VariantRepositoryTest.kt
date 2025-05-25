package de.maulmann.cardcollection.repository

import de.maulmann.cardcollection.model.CardBrand
import de.maulmann.cardcollection.model.CardManufacturer
import de.maulmann.cardcollection.model.CardTheme
import de.maulmann.cardcollection.model.Variant
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class VariantRepositoryTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var variantRepository: VariantRepository

    @Autowired
    private lateinit var cardThemeRepository: CardThemeRepository

    @Autowired
    private lateinit var cardBrandRepository: CardBrandRepository

    @Autowired
    private lateinit var cardManufacturerRepository: CardManufacturerRepository

    @Test
    fun `should save and retrieve variant with theme`() {
        // 1. Create and save Manufacturer
        val manufacturer = CardManufacturer(name = "Test Manufacturer")
        cardManufacturerRepository.save(manufacturer)

        // 2. Create and save Brand
        val brand = CardBrand(name = "Test Brand", manufacturer = manufacturer)
        cardBrandRepository.save(brand)

        // 3. Create and save Theme
        val theme = CardTheme(name = "Test Theme", brand = brand)
        cardThemeRepository.save(theme)

        // 4. Create and save Variant
        val variant = Variant(name = "Test Variant", theme = theme)
        variantRepository.save(variant)

        // Optional: Clear persistence context to ensure data is fetched from DB
        entityManager.flush()
        entityManager.clear()

        // 5. Retrieve the Variant
        val foundVariant = variantRepository.findById(variant.id).orElse(null)

        // 6. Assertions
        assertThat(foundVariant).isNotNull
        assertThat(foundVariant?.name).isEqualTo("Test Variant")
        assertThat(foundVariant?.theme).isNotNull
        assertThat(foundVariant?.theme?.id).isEqualTo(theme.id)
        assertThat(foundVariant?.theme?.name).isEqualTo("Test Theme")
        assertThat(foundVariant?.theme?.brand?.id).isEqualTo(brand.id)
        assertThat(foundVariant?.theme?.brand?.manufacturer?.id).isEqualTo(manufacturer.id)
    }
}
