package de.maulmann.cardcollection.model

import de.maulmann.cardcollection.repository.TeamRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class TeamTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var teamRepository: TeamRepository

    @Test
    fun `should save and retrieve team`() {
        val team = Team(name = "Test Team")
        teamRepository.save(team)

        val foundTeam = teamRepository.findById(team.id).orElse(null)
        assertThat(foundTeam).isEqualTo(team)
    }
}
