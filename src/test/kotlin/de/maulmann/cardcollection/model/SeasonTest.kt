package de.maulmann.cardcollection.model

import de.maulmann.cardcollection.repository.SeasonRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.dao.DataIntegrityViolationException

@DataJpaTest
class SeasonTest {

    @Autowired
    private lateinit var seasonRepository: SeasonRepository

    @Test
    fun `should save and retrieve season`() {
        val season = Season(name = "2023-24")
        seasonRepository.save(season)

        val foundSeason = seasonRepository.findById(season.id).orElse(null)
        assertThat(foundSeason).isEqualTo(season)
    }

    @Test
    fun `should not save season with duplicate name`() {
        val season1 = Season(name = "2023-24")
        seasonRepository.save(season1)

        val season2 = Season(name = "2023-24")
        assertThrows<DataIntegrityViolationException> {
            seasonRepository.saveAndFlush(season2)
        }
    }
}
