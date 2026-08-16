package de.maulmann.cardcollection.service

import de.maulmann.cardcollection.dto.CardFilter
import de.maulmann.cardcollection.model.*
import de.maulmann.cardcollection.repository.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageRequest

@DataJpaTest
@Import(CardService::class)
class CardServiceIntegrationTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var cardService: CardService

    private lateinit var pan: CardManufacturer
    private lateinit var ud: CardManufacturer
    private lateinit var prizm: CardBrand
    private lateinit var baseTheme: CardTheme
    private lateinit var baseVar: Variant
    private lateinit var goldVar: Variant
    private lateinit var season23: Season
    private lateinit var bball: Sport
    private lateinit var soccer: Sport
    private lateinit var player1: Player
    private lateinit var player2: Player
    private lateinit var teamHeat: Team
    private lateinit var teamLakers: Team

    @BeforeEach
    fun setUp() {
        pan = entityManager.persist(CardManufacturer(name = "Panini"))
        ud = entityManager.persist(CardManufacturer(name = "Upper Deck"))
        prizm = entityManager.persist(CardBrand(name = "Prizm"))
        baseTheme = entityManager.persist(CardTheme(name = "Base Set"))
        baseVar = entityManager.persist(Variant(name = "Base"))
        goldVar = entityManager.persist(Variant(name = "Gold /10"))
        season23 = entityManager.persist(Season(name = "2023-24"))
        bball = entityManager.persist(Sport(name = "Basketball"))
        soccer = entityManager.persist(Sport(name = "Soccer"))
        player1 = entityManager.persist(Player(name = "Juwan", surname = "Howard", sport = bball))
        player2 = entityManager.persist(Player(name = "Lionel", surname = "Messi", sport = soccer))
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
            theme = baseTheme
        )
        entityManager.persist(card1)
        entityManager.persist(CardPlayer(id = CardPlayerId(card1.id, player1.id), card = card1, player = player1, team = teamHeat))

        val card2 = Card(
            printRun = 1,
            serialNumber = 1,
            season = season23,
            number = "1",
            rookieCard = true,
            gameUsedMaterial = false,
            autograph = false,
            manufacturer = ud,
            brand = prizm,
            variant = baseVar,
            theme = baseTheme
        )
        entityManager.persist(card2)
        entityManager.persist(CardPlayer(id = CardPlayerId(card2.id, player2.id), card = card2, player = player2, team = teamLakers))

        entityManager.flush()
        entityManager.clear()
    }

    @Test
    fun `test filter by playerId using EXISTS subquery`() {
        val filter = CardFilter(playerId = player1.id)
        val result = cardService.getCardsFiltered(filter, PageRequest.of(0, 10))
        assertThat(result.totalElements).isEqualTo(1)
        assertThat(result.content[0].number).isEqualTo("101")
    }

    @Test
    fun `test filter by sportId using EXISTS subquery`() {
        val filter = CardFilter(sportId = soccer.id)
        val result = cardService.getCardsFiltered(filter, PageRequest.of(0, 10))
        assertThat(result.totalElements).isEqualTo(1)
        assertThat(result.content[0].number).isEqualTo("1")
    }

    @Test
    fun `test filter by teamId using EXISTS subquery`() {
        val filter = CardFilter(teamId = teamHeat.id)
        val result = cardService.getCardsFiltered(filter, PageRequest.of(0, 10))
        assertThat(result.totalElements).isEqualTo(1)
        assertThat(result.content[0].number).isEqualTo("101")
    }

    @Test
    fun `test filter by printRunRange and boolean flags`() {
        val filter = CardFilter(printRunRangeKey = PrintRunRange.LE_10.key, gameUsed = true, autograph = true)
        val result = cardService.getCardsFiltered(filter, PageRequest.of(0, 10))
        assertThat(result.totalElements).isEqualTo(1)
        assertThat(result.content[0].number).isEqualTo("101")
    }
}
