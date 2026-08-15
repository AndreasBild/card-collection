package de.maulmann.cardcollection.repository

import de.maulmann.cardcollection.model.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager

@DataJpaTest
class CardRepositoryTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var cardRepository: CardRepository

    private lateinit var pan: CardManufacturer
    private lateinit var ud: CardManufacturer
    private lateinit var prizm: CardBrand
    private lateinit var spx: CardBrand
    private lateinit var baseTheme: CardTheme
    private lateinit var silverTheme: CardTheme
    private lateinit var baseVar: Variant
    private lateinit var goldVar: Variant
    private lateinit var season23: Season
    private lateinit var season24: Season
    private lateinit var bball: Sport
    private lateinit var player1: Player
    private lateinit var player2: Player
    private lateinit var teamHeat: Team
    private lateinit var teamLakers: Team

    @BeforeEach
    fun setUp() {
        pan = entityManager.persist(CardManufacturer(name = "Panini"))
        ud = entityManager.persist(CardManufacturer(name = "Upper Deck"))
        prizm = entityManager.persist(CardBrand(name = "Prizm"))
        spx = entityManager.persist(CardBrand(name = "SPx"))
        baseTheme = entityManager.persist(CardTheme(name = "Base Set"))
        silverTheme = entityManager.persist(CardTheme(name = "Silver Prizm"))
        baseVar = entityManager.persist(Variant(name = "Base"))
        goldVar = entityManager.persist(Variant(name = "Gold /10"))
        season23 = entityManager.persist(Season(name = "2023-24"))
        season24 = entityManager.persist(Season(name = "2024-25"))
        bball = entityManager.persist(Sport(name = "Basketball"))
        player1 = entityManager.persist(Player(name = "Juwan", surname = "Howard", sport = bball))
        player2 = entityManager.persist(Player(name = "LeBron", surname = "James", sport = bball))
        teamHeat = entityManager.persist(Team(name = "Miami Heat"))
        teamLakers = entityManager.persist(Team(name = "Los Angeles Lakers"))

        val card1 = Card(
            printRun = 10,
            serialNumber = 1,
            season = season23,
            number = "101",
            rookieCard = false,
            gameUsedMaterial = true,
            autograph = true,
            manufacturer = pan,
            brand = prizm,
            variant = goldVar,
            theme = silverTheme
        )
        entityManager.persist(card1)
        entityManager.persist(CardPlayer(id = CardPlayerId(card1.id, player1.id), card = card1, player = player1, team = teamHeat))

        val card2 = Card(
            printRun = 1,
            serialNumber = 1,
            season = season24,
            number = "1",
            rookieCard = true,
            gameUsedMaterial = false,
            autograph = false,
            manufacturer = ud,
            brand = spx,
            variant = baseVar,
            theme = baseTheme
        )
        entityManager.persist(card2)
        entityManager.persist(CardPlayer(id = CardPlayerId(card2.id, player2.id), card = card2, player = player2, team = teamLakers))

        entityManager.flush()
        entityManager.clear()
    }

    @Test
    fun `test findDistinctBrandsByManufacturerId`() {
        val brandsPan = cardRepository.findDistinctBrandsByManufacturerId(pan.id)
        assertThat(brandsPan).hasSize(1)
        assertThat(brandsPan[0].name).isEqualTo("Prizm")

        val allBrands = cardRepository.findDistinctBrandsByManufacturerId(null)
        assertThat(allBrands).hasSize(2)
    }

    @Test
    fun `test findDistinctThemesByManufacturerIdAndBrandId`() {
        val themes = cardRepository.findDistinctThemesByManufacturerIdAndBrandId(pan.id, prizm.id)
        assertThat(themes).hasSize(1)
        assertThat(themes[0].name).isEqualTo("Silver Prizm")

        val allThemes = cardRepository.findDistinctThemesByManufacturerIdAndBrandId(null, null)
        assertThat(allThemes).hasSize(2)
    }

    @Test
    fun `test findDistinctVariantsByFilter`() {
        val variants = cardRepository.findDistinctVariantsByFilter(pan.id, prizm.id, silverTheme.id)
        assertThat(variants).hasSize(1)
        assertThat(variants[0].name).isEqualTo("Gold /10")
    }

    @Test
    fun `test findAllWithDetails`() {
        val cards = cardRepository.findAllWithDetails()
        assertThat(cards).hasSize(2)
        assertThat(cards[0].playerNames).isNotBlank()
        assertThat(cards[0].season).isNotNull()
    }

    @Test
    fun `test findAllBySeasonIdWithDetails`() {
        val cards = cardRepository.findAllBySeasonIdWithDetails(season23.id)
        assertThat(cards).hasSize(1)
        assertThat(cards[0].season.name).isEqualTo("2023-24")
    }
}
