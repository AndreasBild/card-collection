package de.maulmann.cardcollection.repository

import de.maulmann.cardcollection.model.Card
import de.maulmann.cardcollection.model.Player
import de.maulmann.cardcollection.model.Variant
import de.maulmann.cardcollection.model.CardTheme
import de.maulmann.cardcollection.model.CardBrand
import de.maulmann.cardcollection.model.CardManufacturer
import de.maulmann.cardcollection.model.Sport
import de.maulmann.cardcollection.model.Team
import de.maulmann.cardcollection.model.Season // Added import for Season
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
// JPA Buddy will sometimes add this, ensure it's not duplicated if running locally
// import org.springframework.test.context.ActiveProfiles 

@DataJpaTest
// @ActiveProfiles("test") // Optional: If you have a specific test profile
class CardRepositoryTest {

    @Autowired
    lateinit var entityManager: TestEntityManager

    @Autowired
    lateinit var cardRepository: CardRepository

    // Helper function to set up common entities to avoid repetition
    private fun setupCommonEntities(): Triple<Player, CardTheme, Variant> {
        val sport = Sport(name = "TestSportCommon"); entityManager.persistAndFlush(sport)
        val team = Team(name = "TestTeamCommon"); entityManager.persistAndFlush(team)
        val player = Player(name = "TestPlayerCommon", surname = "TestSurnameCommon", team = team, sport = sport); entityManager.persistAndFlush(player)
        val manufacturer = CardManufacturer(name = "TestManuCommon"); entityManager.persist(manufacturer)
        val brand = CardBrand(name = "TestBrandCommon", manufacturer = manufacturer); entityManager.persist(brand)
        val theme = CardTheme(name = "TestThemeCommon", brand = brand); entityManager.persist(theme)
        val variant = Variant(name = "TestVariantCommon"); entityManager.persist(variant) // Removed theme from Variant constructor
        entityManager.flush()
        return Triple(player, theme, variant)
    }

    // Tests for findAllByVariantId, findAllByPrintRun, findAllByPrintRunGreaterThan are removed
    // as these methods were deleted from CardRepository.
    // Existing tests for other CardRepository methods (if any) would remain.
    // For now, this file will be empty of tests until new ones are added for existing methods
    // or if the requirements change.
}
