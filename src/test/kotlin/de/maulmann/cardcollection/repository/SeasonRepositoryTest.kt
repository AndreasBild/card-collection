package de.maulmann.cardcollection.repository

import de.maulmann.cardcollection.model.Season
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class SeasonRepositoryTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var repository: SeasonRepository

    @Test
    fun `findAllByOrderByNameAsc should return seasons sorted by name`() {
        entityManager.persist(Season(name = "2023-24"))
        entityManager.persist(Season(name = "2021-22"))
        entityManager.persist(Season(name = "2022-23"))

        val seasons = repository.findAllByOrderByNameAsc()

        assertEquals(3, seasons.size)
        assertEquals("2021-22", seasons[0].name)
        assertEquals("2022-23", seasons[1].name)
        assertEquals("2023-24", seasons[2].name)
    }
}
