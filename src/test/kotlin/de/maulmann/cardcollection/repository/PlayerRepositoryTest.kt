package de.maulmann.cardcollection.repository

import de.maulmann.cardcollection.model.Player
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager

@DataJpaTest
class PlayerRepositoryTest @Autowired constructor(
    val entityManager: TestEntityManager,
    val playerRepository: PlayerRepository
) {

    @Test
    fun `when findById then return Player`() {
        val player = Player(name = "FirstName", surname = "LastName", sport = null)
        entityManager.persistAndFlush(player)
        val found = playerRepository.findById(player.id)
        assertThat(found.get()).isEqualTo(player)
    }
}
