package de.maulmann.cardcollection.repository

import de.maulmann.cardcollection.model.Team
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class TeamRepositoryTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var repository: TeamRepository

    @Test
    fun `should save and find a team`() {
        val team = Team(name = "New York Knicks")
        val savedTeam = repository.save(team)

        entityManager.flush()
        entityManager.clear()

        val foundTeam = repository.findById(savedTeam.id).orElse(null)

        assertNotNull(foundTeam)
        assertEquals("New York Knicks", foundTeam!!.name)
    }
}
