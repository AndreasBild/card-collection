package de.maulmann.cardcollection.repository

import de.maulmann.cardcollection.model.Player
import de.maulmann.cardcollection.model.Sport
import de.maulmann.cardcollection.model.Team
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class PlayerRepositoryTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var repository: PlayerRepository

    @BeforeEach
    fun setUp() {
        val sport1 = entityManager.persist(Sport(name = "Basketball"))
        val team1 = entityManager.persist(Team(name = "Lakers"))
        entityManager.persist(Player(name = "LeBron", surname = "James", sport = sport1, team = team1))

        val sport2 = entityManager.persist(Sport(name = "Football"))
        val team2 = entityManager.persist(Team(name = "Chiefs"))
        entityManager.persist(Player(name = "Patrick", surname = "Mahomes", sport = sport2, team = team2))

        entityManager.persist(Player(name = "Kobe", surname = "Bryant", sport = sport1, team = team1))
    }

    @Test
    fun `findAllWithTeamAndSportSorted should return players with fetched relations and sorted`() {
        val players = repository.findAllWithTeamAndSportSorted()
        assertEquals(3, players.size)

        // Verify sorting by surname, then name
        assertEquals("Bryant", players[0].surname)
        assertEquals("James", players[1].surname)
        assertEquals("Mahomes", players[2].surname)

        // Verify that team and sport are fetched
        assertEquals("Lakers", players[0].team!!.name)
        assertEquals("Basketball", players[0].sport!!.name)
    }
}
