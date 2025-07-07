package de.maulmann.cardcollection.repository

import de.maulmann.cardcollection.model.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@ActiveProfiles("test") // Ensure test properties are used (e.g., H2)
class CardGradingIntegrationTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var cardRepository: CardRepository

    @Autowired
    private lateinit var gradingRepository: GradingRepository

    // Dummy entities needed for Card. These would ideally be pre-populated or handled by a test data setup utility.
    private lateinit var season: Season
    private lateinit var player: Player
    private lateinit var variant: Variant
    private lateinit var theme: CardTheme
    private lateinit var sport: Sport
    private lateinit var team: Team
    private lateinit var brand: CardBrand
    private lateinit var manufacturer: CardManufacturer


    @BeforeEach
    fun setUp() {
        // Minimal setup for related entities to satisfy NotNull constraints on Card
        // In a real scenario, these would be more robustly managed.
        manufacturer = entityManager.persistFlushFind(CardManufacturer(name = "Test Manufacturer"))
        brand = entityManager.persistFlushFind(CardBrand(name = "Test Brand", manufacturer = manufacturer))
        theme = entityManager.persistFlushFind(CardTheme(name = "Test Theme", brand = brand))
        sport = entityManager.persistFlushFind(Sport(name = "Test Sport"))
        team = entityManager.persistFlushFind(Team(name = "Test Team", sport = sport))
        season = entityManager.persistFlushFind(Season(name = "2023-24"))
        variant = entityManager.persistFlushFind(Variant(name = "Base"))
        player = entityManager.persistFlushFind(Player(name = "Test", surname = "Player", team = team, sport = sport))
    }

    @Test
    fun `should save and retrieve card with grading`() {
        val grading = Grading(grade = 9.5f, gradingCompany = GradingCompany.PSA)
        // We don't save grading directly here if Card owns the cascade.
        // entityManager.persist(grading) // Not needed if CascadeType.ALL is on Card.grading

        val card = Card(
            printRun = 100,
            serialNumber = 10,
            season = season,
            number = "1",
            rookieCard = false,
            gameUsedMaterial = false,
            autograph = false,
            player = player,
            variant = variant,
            theme = theme,
            grading = grading // Associate grading
        )

        val savedCard = cardRepository.save(card)
        entityManager.flush() // Ensure persistence
        entityManager.clear() // Clear persistence context to force reload from DB

        val foundCard = cardRepository.findById(savedCard.id).orElse(null)

        assertNotNull(foundCard)
        assertNotNull(foundCard.grading)
        assertEquals(9.5f, foundCard.grading?.grade)
        assertEquals(GradingCompany.PSA, foundCard.grading?.gradingCompany)
        assertNotNull(foundCard.grading?.id)

        // Verify grading_id is set on card table (indirectly by checking foundCard.grading.id)
        // And verify the grading entity was persisted
        val foundGrading = gradingRepository.findById(foundCard.grading!!.id).orElse(null)
        assertNotNull(foundGrading)
        assertEquals(9.5f, foundGrading?.grade)
    }

    @Test
    fun `should save and retrieve card without grading`() {
        val card = Card(
            printRun = 50,
            serialNumber = 5,
            season = season,
            number = "2",
            rookieCard = true,
            gameUsedMaterial = false,
            autograph = false,
            player = player,
            variant = variant,
            theme = theme,
            grading = null // No grading
        )

        val savedCard = cardRepository.save(card)
        entityManager.flush()
        entityManager.clear()

        val foundCard = cardRepository.findById(savedCard.id).orElse(null)

        assertNotNull(foundCard)
        assertNull(foundCard.grading)
    }

    @Test
    fun `when card with grading is deleted, grading is also deleted due to Cascade ALL`() {
        val grading = Grading(grade = 7.0f, gradingCompany = GradingCompany.BGS)
        val card = Card(
            printRun = 10,
            serialNumber = 1,
            season = season,
            number = "3",
            rookieCard = false,
            gameUsedMaterial = true,
            autograph = true,
            player = player,
            variant = variant,
            theme = theme,
            grading = grading
        )

        val savedCard = cardRepository.save(card)
        entityManager.flush()
        val savedGradingId = savedCard.grading!!.id

        // Ensure grading exists
        assertTrue(gradingRepository.existsById(savedGradingId))

        cardRepository.deleteById(savedCard.id)
        entityManager.flush()
        entityManager.clear()

        assertFalse(cardRepository.existsById(savedCard.id))
        // Due to CascadeType.ALL on Card.grading, the Grading entity should also be deleted.
        assertFalse(gradingRepository.existsById(savedGradingId))
    }

    @Test
    fun `updating a card's grading should work`() {
        val initialGrading = Grading(grade = 8.0f, gradingCompany = GradingCompany.PSA)
        var card = Card(
            printRun = 25, serialNumber = 2, season = season, number = "4",
            rookieCard = false, gameUsedMaterial = false, autograph = false,
            player = player, variant = variant, theme = theme, grading = initialGrading
        )
        card = cardRepository.saveAndFlush(card)
        entityManager.clear() // Detach to simulate a new transaction/context

        val cardToUpdate = cardRepository.findById(card.id).orElseThrow()
        assertNotNull(cardToUpdate.grading)
        val oldGradingId = cardToUpdate.grading!!.id

        // Create a new Grading instance for update
        val newGrading = Grading(grade = 9.0f, gradingCompany = GradingCompany.BGS)
        cardToUpdate.grading = newGrading // Assign new grading object

        val updatedCard = cardRepository.saveAndFlush(cardToUpdate)
        entityManager.clear()

        val refetchedCard = cardRepository.findById(updatedCard.id).orElseThrow()
        assertNotNull(refetchedCard.grading)
        assertEquals(9.0f, refetchedCard.grading?.grade)
        assertEquals(GradingCompany.BGS, refetchedCard.grading?.gradingCompany)
        assertNotEquals(oldGradingId, refetchedCard.grading?.id, "A new grading entity should be created, and the old one orphaned if not handled by orphanRemoval=true")

        // Check if the old grading is still in the DB (it should be if orphanRemoval=false, which is default)
        // or deleted if orphanRemoval=true on the @OneToOne mapping.
        // With current setup (CascadeType.ALL, no orphanRemoval=true), the old grading might become orphaned.
        // If orphanRemoval = true was set on Card.grading:
        // assertFalse(gradingRepository.existsById(oldGradingId), "Old grading should be removed if orphanRemoval=true")
        // If not, it will remain. This is acceptable for this test case, focusing on update.
        assertTrue(gradingRepository.existsById(oldGradingId), "Old grading should still exist as orphanRemoval is not true by default.")
    }
}
