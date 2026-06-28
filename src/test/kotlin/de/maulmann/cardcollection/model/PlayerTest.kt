package de.maulmann.cardcollection.model

import de.maulmann.cardcollection.repository.PlayerRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager

@DataJpaTest
class PlayerTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var playerRepository: PlayerRepository

    @Test
    fun `should save and retrieve player`() {
        val sport = Sport(name = "Test Sport")
        entityManager.persist(sport)

        val team = Team(name = "Test Team")
        entityManager.persist(team)

        val player = Player(name = "Test", surname = "Player", teams = mutableSetOf(team), sport = sport)
        playerRepository.save(player)

        val foundPlayer = playerRepository.findById(player.id).orElse(null)
        assertThat(foundPlayer).isEqualTo(player)
    }

    @Test
    fun `should save player with null team and sport`() {
        val player = Player(name = "Test", surname = "Player", teams = mutableSetOf(), sport = null)
        playerRepository.save(player)

        val foundPlayer = playerRepository.findById(player.id).orElse(null)
        assertThat(foundPlayer).isEqualTo(player)
    }
}
