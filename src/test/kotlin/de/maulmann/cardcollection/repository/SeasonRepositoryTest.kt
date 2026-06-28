package de.maulmann.cardcollection.repository

import de.maulmann.cardcollection.model.Season
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager

@DataJpaTest
class SeasonRepositoryTest @Autowired constructor(
    val entityManager: TestEntityManager,
    val seasonRepository: SeasonRepository
) {

    @Test
    fun `when findById then return Season`() {
        val season = Season(name = "2022-23")
        entityManager.persistAndFlush(season)
        val found = seasonRepository.findById(season.id)
        assertThat(found.get()).isEqualTo(season)
    }
}
