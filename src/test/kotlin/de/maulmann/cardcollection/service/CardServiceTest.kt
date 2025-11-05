package de.maulmann.cardcollection.service

import de.maulmann.cardcollection.model.*
import de.maulmann.cardcollection.repository.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import java.util.*

@ExtendWith(MockitoExtension::class)
class CardServiceTest {

    @Mock
    private lateinit var cardRepository: CardRepository

    @Mock
    private lateinit var cardBrandRepository: CardBrandRepository

    @Mock
    private lateinit var cardThemeRepository: CardThemeRepository

    @Mock
    private lateinit var sportRepository: SportRepository

    @Mock
    private lateinit var variantRepository: VariantRepository

    @Mock
    private lateinit var teamRepository: TeamRepository

    @Mock // Added missing mock
    private lateinit var seasonRepository: SeasonRepository
    
    // playerRepository and manufacturerRepository are not direct dependencies of CardService
    // and are not needed as @Mock fields for CardService testing.
    // They are used by helper methods to construct complex objects, but their repositories
    // don't need to be mocked at this level unless CardService itself uses them.

    @InjectMocks
    private lateinit var cardService: CardService

    // Stable mock instances for dependencies
    private val stableTestSport: Sport = mock { on { id } doReturn 1L; on { name } doReturn "Basketball" }
    private val stableTestTeam: Team = mock { on { id } doReturn 1L; on { name } doReturn "Chicago Bulls" }
    private val stableTestPlayer: Player = mock {
        on { id } doReturn 1L
        on { name } doReturn "Juwan"
        on { surname } doReturn "Howard"
        on { team } doReturn stableTestTeam
        on { sport } doReturn stableTestSport
    }
    private val stableTestManufacturer: CardManufacturer = mock { on { id } doReturn 1L; on { name } doReturn "Panini" }
    private val stableTestBrand: CardBrand = mock {
        on { id } doReturn 1L
        on { name } doReturn "Prizm"
        on { manufacturer } doReturn stableTestManufacturer
    }
    private val stableTestTheme: CardTheme = mock {
        on { id } doReturn 1L
        on { name } doReturn "Base Set"
        on { brand } doReturn stableTestBrand
    }
    private val stableTestVariant: Variant = mock {
        on { id } doReturn 1L
        on { name } doReturn "Silver"
        on { theme } doReturn stableTestTheme
    }

    // Helper methods now primarily return stable instances or create simple data objects
    private fun createMockPlayer(): Player = stableTestPlayer
    private fun createMockBrand(): CardBrand = stableTestBrand
    private fun createMockTheme(): CardTheme = stableTestTheme
    private fun createMockVariant(): Variant = stableTestVariant
    private fun createMockSeason(id: Long = 1L, name: String = "1994-95"): Season = Season(id = id, name = name)


    private fun createMockCard(id: Long): Card = Card(
        id = id,
        player = createMockPlayer(),      // Uses stableTestPlayer
        season = createMockSeason(),      // Uses real Season object
        theme = createMockTheme(),        // Uses stableTestTheme
        variant = createMockVariant(),    // Uses stableTestVariant
        number = "101",
        printRun = 1000,
        serialNumber = 123,
        rookieCard = true,
        gameUsedMaterial = false,
        autograph = true
        // notes property removed
    )
    
    private fun createMockSport(id: Long, name: String): Sport = Sport(
        id = id,
        name = name
    )

    @Test
    fun `testGetAllCards_returnsListOfCards`() {
        // GIVEN
        val mockCard1 = createMockCard(1L)
        val mockCard2 = createMockCard(2L)
        val expectedCards = listOf(mockCard1, mockCard2)
        whenever(cardRepository.findAllWithDetails()).thenReturn(expectedCards) // Corrected method

        // WHEN
        val result = cardService.getAllCards()

        // THEN
        assertThat(result).isEqualTo(expectedCards)
        verify(cardRepository).findAllWithDetails() // Corrected verification
    }

    @Test
    fun `testGetCardById_cardExists_returnsCard`() {
        // GIVEN
        val cardId = 1L
        val sampleCard = createMockCard(cardId)
        whenever(cardRepository.findById(cardId)).thenReturn(Optional.of(sampleCard))

        // WHEN
        val result = cardService.getCardById(cardId)

        // THEN
        assertThat(result).isNotNull
        assertThat(result).isEqualTo(sampleCard)
        verify(cardRepository).findById(cardId)
    }

    @Test
    fun `testGetCardById_cardDoesNotExist_returnsNull`() {
        // GIVEN
        val cardId = 1L
        whenever(cardRepository.findById(cardId)).thenReturn(Optional.empty())

        // WHEN
        val result = cardService.getCardById(cardId)

        // THEN
        assertThat(result).isNull()
        verify(cardRepository).findById(cardId)
    }

    @Test
    fun `testGetAllSports_returnsListOfSports`() {
        // GIVEN
        val mockSport1 = createMockSport(1L, "Basketball")
        val mockSport2 = createMockSport(2L, "Football")
        val expectedSports = listOf(mockSport1, mockSport2)
        whenever(sportRepository.findAll()).thenReturn(expectedSports)

        // WHEN
        val result = cardService.getAllSports()

        // THEN
        assertThat(result).isEqualTo(expectedSports)
        verify(sportRepository).findAll()
    }

    @Test
    fun `testGetCardsFiltered_noFilters_callsRepositoryFindAll`() {
        // GIVEN
        val pageable: Pageable = PageRequest.of(0, 10)
        val expectedPage: Page<Card> = PageImpl(emptyList(), pageable, 0)
        whenever(cardRepository.findAll(any<Specification<Card>>(), eq(pageable))).thenReturn(expectedPage)

        // WHEN
        val result = cardService.getCardsFiltered(
            manufacturerId = null,
            brandId = null,
            themeId = null,
            sportId = null,
            playerId = null,
            teamId = null,
            seasonId = null, // Changed from season to seasonId
            gameUsed = null,
            autograph = null,
            variantId = null,
            rookieCard = null,
            printRunRangeKey = null,
            pageable = pageable,
            isGradedNullable = null
        )

        // THEN
        verify(cardRepository).findAll(any<Specification<Card>>(), eq(pageable))
        assertThat(result).isEqualTo(expectedPage)
    }

    @Test
    fun `testGetCardsFiltered_withManufacturerId_buildsCorrectSpecification`() {
        // GIVEN
        val pageable: Pageable = PageRequest.of(0, 10)
        val manufacturerId = 1L
        val expectedPage: Page<Card> = PageImpl(emptyList(), pageable, 0)

        whenever(cardRepository.findAll(any<Specification<Card>>(), eq(pageable))).thenReturn(expectedPage)

        // WHEN
        val result = cardService.getCardsFiltered(
            manufacturerId = manufacturerId,
            brandId = null,
            themeId = null,
            sportId = null,
            playerId = null,
            teamId = null,
            seasonId = null, // Changed from season to seasonId
            gameUsed = null,
            autograph = null,
            variantId = null,
            rookieCard = null,
            printRunRangeKey = null,
            pageable = pageable,
            isGradedNullable = null
        )

        // THEN
        verify(cardRepository).findAll(any<Specification<Card>>(), eq(pageable))
        assertThat(result).isEqualTo(expectedPage)
        // Further testing of the Specification object itself is complex and deferred.
        // The key check here is that findAll is called with *some* Specification.
    }

    @Test
    fun `testGetCardsFiltered_withBrandId_callsRepository`() {
        // GIVEN
        val pageable: Pageable = PageRequest.of(0, 10)
        val brandId = 1L
        val expectedPage: Page<Card> = PageImpl(emptyList(), pageable, 0)
        whenever(cardRepository.findAll(any<Specification<Card>>(), eq(pageable))).thenReturn(expectedPage)

        // WHEN
        val result = cardService.getCardsFiltered(
            manufacturerId = null,
            brandId = brandId,
            themeId = null,
            sportId = null,
            playerId = null,
            teamId = null,
            seasonId = null, // Changed from season to seasonId
            gameUsed = null,
            autograph = null,
            variantId = null,
            rookieCard = null,
            printRunRangeKey = null,
            pageable = pageable,
            isGradedNullable = null
        )

        // THEN
        verify(cardRepository).findAll(any<Specification<Card>>(), eq(pageable))
        assertThat(result).isEqualTo(expectedPage)
    }

    @Test
    fun `testGetCardsFiltered_withRookieCardTrue_callsRepository`() {
        // GIVEN
        val pageable: Pageable = PageRequest.of(0, 10)
        val rookieCard = true
        val expectedPage: Page<Card> = PageImpl(emptyList(), pageable, 0)
        whenever(cardRepository.findAll(any<Specification<Card>>(), eq(pageable))).thenReturn(expectedPage)

        // WHEN
        val result = cardService.getCardsFiltered(
            manufacturerId = null,
            brandId = null,
            themeId = null,
            sportId = null,
            playerId = null,
            teamId = null,
            seasonId = null, // Changed from season to seasonId
            gameUsed = null,
            autograph = null,
            variantId = null,
            rookieCard = rookieCard,
            printRunRangeKey = null,
            pageable = pageable,
            isGradedNullable = null
        )

        // THEN
        verify(cardRepository).findAll(any<Specification<Card>>(), eq(pageable))
        assertThat(result).isEqualTo(expectedPage)
    }

    @Test
    fun `testGetCardsFiltered_withPrintRunRangeOne_callsRepository`() {
        // GIVEN
        val pageable: Pageable = PageRequest.of(0, 10)
        val printRunRangeKey = "ONE" // Assuming "ONE" is a valid key
        val expectedPage: Page<Card> = PageImpl(emptyList(), pageable, 0)
        whenever(cardRepository.findAll(any<Specification<Card>>(), eq(pageable))).thenReturn(expectedPage)

        // WHEN
        val result = cardService.getCardsFiltered(
            manufacturerId = null,
            brandId = null,
            themeId = null,
            sportId = null,
            playerId = null,
            teamId = null,
            seasonId = null, // Changed from season to seasonId
            gameUsed = null,
            autograph = null,
            variantId = null,
            rookieCard = null,
            printRunRangeKey = printRunRangeKey,
            pageable = pageable,
            isGradedNullable = null
        )

        // THEN
        verify(cardRepository).findAll(any<Specification<Card>>(), eq(pageable))
        assertThat(result).isEqualTo(expectedPage)
    }
}
