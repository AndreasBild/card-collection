package de.maulmann.cardcollection.service

import de.maulmann.cardcollection.model.*
import de.maulmann.cardcollection.repository.CardRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionSynchronizationManager
import org.springframework.transaction.support.TransactionTemplate
import java.io.ByteArrayOutputStream

/**
 * Runs the export WITHOUT an active transaction (as in production, where CardExportService is not transactional)
 * to prove that findAllWithDetails() initialises everything the mapping needs.
 */
@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class CardExportServiceIntegrationTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var cardRepository: CardRepository

    @Autowired
    private lateinit var transactionManager: PlatformTransactionManager

    private lateinit var tx: TransactionTemplate

    @BeforeEach
    fun setUp() {
        tx = TransactionTemplate(transactionManager)
        tx.executeWithoutResult {
            val sport = entityManager.persist(Sport(name = "Basketball"))
            val team = entityManager.persist(Team(name = "Washington Bullets"))
            val player = entityManager.persist(Player(name = "Juwan", surname = "Howard", sport = sport))
            val grading = entityManager.persist(Grading(grade = 9.5f, gradingCompany = GradingCompany.PSA))
            val card = entityManager.persist(
                Card(
                    grading = grading,
                    gradingCertNumber = "12345678",
                    printRun = 99,
                    serialNumber = 7,
                    season = entityManager.persist(Season(name = "1994-95")),
                    number = "278",
                    rookieCard = true,
                    gameUsedMaterial = false,
                    autograph = true,
                    manufacturer = entityManager.persist(CardManufacturer(name = "Fleer")),
                    brand = entityManager.persist(CardBrand(name = "Ultra")),
                    variant = entityManager.persist(Variant(name = "Gold")),
                    theme = entityManager.persist(CardTheme(name = "Base Set"))
                )
            )
            entityManager.persist(CardPlayer(CardPlayerId(card.id, player.id), card, player, team))
            entityManager.flush()
            entityManager.clear()
        }
    }

    @AfterEach
    fun cleanUp() {
        tx.executeWithoutResult {
            listOf(
                "CardPlayer", "Card", "Grading", "Player", "Team", "Sport",
                "Season", "CardManufacturer", "CardBrand", "CardTheme", "Variant"
            ).forEach { entityManager.entityManager.createQuery("delete from $it").executeUpdate() }
        }
    }

    @Test
    fun `export maps all relations without an active transaction`() {
        assertThat(TransactionSynchronizationManager.isActualTransactionActive()).isFalse()
        val service = CardExportService(cardRepository)

        val dtos = service.exportAllCardsToJsonDtos()

        assertThat(dtos).hasSize(1)
        val dto = dtos.single()
        assertThat(dto.player).isEqualTo("Juwan Howard")
        assertThat(dto.team).isEqualTo("Washington Bullets")
        assertThat(dto.season).isEqualTo("1994-95")
        assertThat(dto.company).isEqualTo("Fleer")
        assertThat(dto.brand).isEqualTo("Ultra")
        assertThat(dto.variant).isEqualTo("Gold")
        assertThat(dto.gradingCompany).isEqualTo("PSA")
        assertThat(dto.grade).isEqualTo("9.5")
        assertThat(dto.gradingUrl).isEqualTo("https://www.psacard.com/cert/12345678")
    }

    @Test
    fun `csv and json streaming work without an active transaction`() {
        val service = CardExportService(cardRepository)

        val csv = ByteArrayOutputStream().also { service.writeCardsCsv(it) }.toString(Charsets.UTF_8)
        val json = ByteArrayOutputStream().also { service.writeCardsJson(it, null) }.toString(Charsets.UTF_8)

        assertThat(csv).contains("Juwan Howard").contains("Washington Bullets")
        assertThat(json).contains("Juwan Howard").contains("1994-95")
    }
}
