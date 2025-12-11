package de.maulmann.cardcollection.model

import de.maulmann.cardcollection.repository.SportRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class SportTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var sportRepository: SportRepository

    @Test
    fun `should save and retrieve sport`() {
        val sport = Sport(name = "Test Sport")
        sportRepository.save(sport)

        val foundSport = sportRepository.findById(sport.id).orElse(null)
        assertThat(foundSport).isEqualTo(sport)
    }
}
