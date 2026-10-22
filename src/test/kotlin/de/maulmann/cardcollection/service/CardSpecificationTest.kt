package de.maulmann.cardcollection.service

import de.maulmann.cardcollection.dto.CardFilter
import de.maulmann.cardcollection.model.*
import de.maulmann.cardcollection.repository.CardRepository
import org.assertj.core.api.Assertions.assertThat
import org.hibernate.Hibernate
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

@DataJpaTest
class CardSpecificationTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var cardRepository: CardRepository

    private lateinit var heat: Team
    private lateinit var juwan: Player
    private lateinit var lebron: Player
    private lateinit var football: Sport
    private lateinit var season: Season

    @BeforeEach
    fun setUp() {
        val basketball = entityManager.persist(Sport(name = "Basketball"))
        football = entityManager.persist(Sport(name = "Football"))
        heat = entityManager.persist(Team(name = "Miami Heat"))
        juwan = entityManager.persist(Player(name = "Juwan", surname = "Howard", sport = basketball))
        lebron = entityManager.persist(Player(name = "LeBron", surname = "James", sport = football))
        season = entityManager.persist(Season(name = "2023-24"))

        // card 1: Juwan + LeBron, graded, rookie, print run 10 (two players => a join would duplicate rows)
        val card1 = newCard(number = "1", printRun = 10, rookie = true, graded = true)
        addPlayer(card1, juwan, heat)
        addPlayer(card1, lebron, null)
        // card 2: Juwan only, no print run
        val card2 = newCard(number = "2", printRun = null, rookie = false, graded = false)
        addPlayer(card2, juwan, null)
        // card 3: LeBron only, print run 500
        val card3 = newCard(number = "3", printRun = 500, rookie = false, graded = false)
        addPlayer(card3, lebron, null)

        entityManager.flush()
        entityManager.clear()
    }

    private fun newCard(number: String, printRun: Int?, rookie: Boolean, graded: Boolean): Card {
        val grading = if (graded) {
            entityManager.persist(Grading(grade = 9.0f, gradingCompany = GradingCompany.PSA))
        } else null
        return entityManager.persist(
            Card(
                grading = grading,
                printRun = printRun,
                serialNumber = 0,
                season = season,
                number = number,
                rookieCard = rookie,
                gameUsedMaterial = false,
                autograph = false,
                manufacturer = entityManager.persist(CardManufacturer(name = "Maker $number")),
                brand = entityManager.persist(CardBrand(name = "Brand $number")),
                variant = entityManager.persist(Variant(name = "Variant $number")),
                theme = entityManager.persist(CardTheme(name = "Theme $number"))
            )
        )
    }

    private fun addPlayer(card: Card, player: Player, team: Team?) {
        entityManager.persist(CardPlayer(CardPlayerId(card.id, player.id), card, player, team))
    }

    private fun numbers(filter: CardFilter): List<String> =
        cardRepository.findAll(CardSpecifications.fromFilter(filter), PageRequest.of(0, 10, Sort.by("number")))
            .content.map { it.number }

    @Test
    fun `empty filter returns all cards`() {
        assertThat(numbers(CardFilter())).containsExactly("1", "2", "3")
    }

    @Test
    fun `player filter does not duplicate cards with several players`() {
        assertThat(numbers(CardFilter(playerId = juwan.id))).containsExactly("1", "2")
        assertThat(numbers(CardFilter(playerId = lebron.id))).containsExactly("1", "3")
    }

    @Test
    fun `sport and team filters use card player relation`() {
        assertThat(numbers(CardFilter(sportId = football.id))).containsExactly("1", "3")
        assertThat(numbers(CardFilter(teamId = heat.id))).containsExactly("1")
    }

    @Test
    fun `boolean and graded filters`() {
        assertThat(numbers(CardFilter(rookieCard = true))).containsExactly("1")
        assertThat(numbers(CardFilter(isGradedNullable = true))).containsExactly("1")
        assertThat(numbers(CardFilter(isGradedNullable = false))).containsExactly("2", "3")
    }

    @Test
    fun `print run range filters`() {
        assertThat(numbers(CardFilter(printRunRangeKey = PrintRunRange.LE_10.key))).containsExactly("1")
        assertThat(numbers(CardFilter(printRunRangeKey = PrintRunRange.LE_1000.key))).containsExactly("1", "3")
        assertThat(numbers(CardFilter(printRunRangeKey = PrintRunRange.ALL_WITH_PRINT_RUN.key)))
            .containsExactly("1", "3")
    }

    @Test
    fun `count query is not fetch joined and total reflects filter`() {
        val page = cardRepository.findAll(
            CardSpecifications.fromFilter(CardFilter(playerId = juwan.id)),
            PageRequest.of(0, 1, Sort.by("number"))
        )

        assertThat(page.totalElements).isEqualTo(2)
        assertThat(page.content).hasSize(1)
    }

    @Test
    fun `entity query fetches to-one relations eagerly`() {
        val card = cardRepository.findAll(
            CardSpecifications.fromFilter(CardFilter()),
            PageRequest.of(0, 10, Sort.by("number"))
        ).content.first()
        entityManager.clear()

        assertThat(Hibernate.isInitialized(card.season)).isTrue()
        assertThat(Hibernate.isInitialized(card.manufacturer)).isTrue()
        assertThat(Hibernate.isInitialized(card.brand)).isTrue()
        assertThat(Hibernate.isInitialized(card.theme)).isTrue()
        assertThat(Hibernate.isInitialized(card.variant)).isTrue()
        assertThat(Hibernate.isInitialized(card.grading)).isTrue()
    }
}

