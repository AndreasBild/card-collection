package de.maulmann.cardcollection.repository

import de.maulmann.cardcollection.model.CardManufacturer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class ActualCardManufacturerRepositoryTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var repository: ActualCardManufacturerRepository

    @Test
    fun `should save and find a card manufacturer`() {
        val manufacturer = CardManufacturer(name = "Panini")
        val savedManufacturer = repository.save(manufacturer)

        entityManager.flush()
        entityManager.clear()

        val foundManufacturer = repository.findById(savedManufacturer.id).orElse(null)

        assertNotNull(foundManufacturer)
        assertEquals("Panini", foundManufacturer!!.name)
    }
}
