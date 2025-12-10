package de.maulmann.cardcollection.repository

import de.maulmann.cardcollection.model.Sport
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class SportRepositoryTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var repository: SportRepository

    @Test
    fun `should save and find a sport`() {
        val sport = Sport(name = "Soccer")
        val savedSport = repository.save(sport)

        entityManager.flush()
        entityManager.clear()

        val foundSport = repository.findById(savedSport.id).orElse(null)

        assertNotNull(foundSport)
        assertEquals("Soccer", foundSport!!.name)
    }
}
