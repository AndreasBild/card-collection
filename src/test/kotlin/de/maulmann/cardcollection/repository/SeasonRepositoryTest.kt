package de.maulmann.cardcollection.repository

import de.maulmann.cardcollection.model.Season
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class SeasonRepositoryTest @Autowired constructor(
    val entityManager: TestEntityManager,
    val seasonRepository: SeasonRepository
) {

    @Test
    fun `when findById then return Season`() {
        val season = Season(name = "2022-23")
        entityManager.persistAndFlush(season)
        val found = seasonRepository.findById(season.id!!)
        assertThat(found.get()).isEqualTo(season)
    }
}
