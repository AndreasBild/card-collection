package de.maulmann.cardcollection.service

import de.maulmann.cardcollection.model.*
import de.maulmann.cardcollection.repository.CardBrandRepository
import de.maulmann.cardcollection.repository.CardRepository
import de.maulmann.cardcollection.repository.CardThemeRepository
import de.maulmann.cardcollection.repository.SportRepository
import de.maulmann.cardcollection.repository.VariantRepository
import de.maulmann.cardcollection.service.PrintRunRange // Import PrintRunRange
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class CardServiceTests {

    @Mock
    private lateinit var cardRepository: CardRepository

    @Mock
    private lateinit var cardBrandRepository: CardBrandRepository // Keep if service uses it

    @Mock
    private lateinit var cardThemeRepository: CardThemeRepository // Keep if service uses it

    @Mock
    private lateinit var sportRepository: SportRepository // Keep if service uses it

    @Mock
    private lateinit var variantRepository: VariantRepository

    @Mock
    private lateinit var teamRepository: TeamRepository // New mock for TeamRepository

    @InjectMocks
    private lateinit var cardService: CardService

    private lateinit var sampleCards: List<Card>

    // Helper entities (can be expanded or moved to a setup method if more complex)
    private val sport1 = Sport(1, "Basketball")
    private val team1 = Team(1, "Chicago Bulls") // Player1's team
    private val team2 = Team(2, "Los Angeles Lakers") // Player2's team for some cards
    private val player1 = Player(1, "Michael", "Jordan", team1, sport1)
    private val player2 = Player(2, "Scottie", "Pippen", team2, sport1) // player2 now primarily on team2

    private val manufacturer1 = CardManufacturer(1, "Upper Deck")
    private val manufacturer2 = CardManufacturer(2, "Panini")

    private val brand1 = CardBrand(1, "Exquisite Collection", manufacturer1)
    private val brand2 = CardBrand(2, "Prizm", manufacturer2)

    private val theme1 = CardTheme(1, "Base Set", brand1)
    private val theme2 = CardTheme(2, "Rookie Autographs", brand2)
    private val theme3 = CardTheme(3, "Game Jersey", brand1)


    private val variant1 = Variant(1, "Gold")
    private val variant2 = Variant(2, "Silver")


    @BeforeEach
    fun setUp() {
        // Reset mocks if needed, though @ExtendWith(MockitoExtension::class) handles this for @Mock fields
        // Mockito.reset(cardRepository, cardBrandRepository, cardThemeRepository, sportRepository)

        // Updated sampleCards for print run testing
        sampleCards = listOf(
            Card(id = 1, player = player1, theme = theme1, variant = variant1, printRun = 1, serialNumber = 1, season = "S1", number = "N1", rookieCard = false, gameUsedMaterial = false, autograph = false),
            Card(id = 2, player = player1, theme = theme1, variant = variant1, printRun = 5, serialNumber = 1, season = "S2", number = "N2", rookieCard = true, gameUsedMaterial = false, autograph = false), // LE_10
            Card(id = 3, player = player1, theme = theme1, variant = variant1, printRun = 10, serialNumber = 1, season = "S3", number = "N3", rookieCard = false, gameUsedMaterial = true, autograph = false), // LE_10
            Card(id = 4, player = player1, theme = theme1, variant = variant1, printRun = 25, serialNumber = 1, season = "S4", number = "N4", rookieCard = true, gameUsedMaterial = true, autograph = false), // LE_50
            Card(id = 5, player = player1, theme = theme1, variant = variant1, printRun = 50, serialNumber = 1, season = "S5", number = "N5", rookieCard = false, gameUsedMaterial = false, autograph = true), // LE_50
            Card(id = 6, player = player1, theme = theme1, variant = variant1, printRun = 75, serialNumber = 1, season = "S6", number = "N6", rookieCard = true, gameUsedMaterial = false, autograph = true), // LE_100
            Card(id = 7, player = player1, theme = theme1, variant = variant1, printRun = 100, serialNumber = 1, season = "S7", number = "N7", rookieCard = false, gameUsedMaterial = true, autograph = true), // LE_100
            Card(id = 8, player = player1, theme = theme1, variant = variant1, printRun = 200, serialNumber = 1, season = "S8", number = "N8", rookieCard = true, gameUsedMaterial = true, autograph = true), // LE_500
            Card(id = 9, player = player1, theme = theme1, variant = variant1, printRun = 500, serialNumber = 1, season = "S9", number = "N9", rookieCard = false, gameUsedMaterial = false, autograph = false), // LE_500
            Card(id = 10, player = player1, theme = theme1, variant = variant1, printRun = 600, serialNumber = 1, season = "S10", number = "N10", rookieCard = true, gameUsedMaterial = false, autograph = false), // LE_1000
            Card(id = 11, player = player1, theme = theme1, variant = variant1, printRun = 1000, serialNumber = 1, season = "S11", number = "N11", rookieCard = false, gameUsedMaterial = true, autograph = false), // LE_1000 (Player1, Team1)
            Card(id = 12, player = player2, theme = theme2, variant = variant2, printRun = 1200, serialNumber = 1, season = "S12", number = "N12", rookieCard = true, gameUsedMaterial = true, autograph = false), // > 1000 (Player2, Team2)
            Card(id = 13, player = player2, theme = theme2, variant = variant2, printRun = 0, serialNumber = 0, season = "S13", number = "N13", rookieCard = false, gameUsedMaterial = false, autograph = false), // Card with printRun = 0 (Player2, Team2)
            // Add a card with player1 (team1) to ensure team filtering works
            Card(id = 14, player = player1, theme = theme2, variant = variant2, printRun = 50, serialNumber = 2, season = "S14", number = "N14", rookieCard = true, gameUsedMaterial = false, autograph = true) // (Player1, Team1)
        )
    }

    @Test
    fun `findAllByGameUsedMaterial when true should call repository and return cards`() {
        val gameUsedCards = sampleCards.filter { it.gameUsedMaterial }
        `when`(cardRepository.findAllByGameUsedMaterial(true)).thenReturn(gameUsedCards)

        val result = cardService.findAllByGameUsedMaterial(true)

        assertEquals(gameUsedCards.size, result.size)
        assertTrue(result.all { it.gameUsedMaterial })
        verify(cardRepository).findAllByGameUsedMaterial(true)
    }

    @Test
    fun `findAllByGameUsedMaterial when false should call repository and return cards`() {
        val nonGameUsedCards = sampleCards.filter { !it.gameUsedMaterial }
        `when`(cardRepository.findAllByGameUsedMaterial(false)).thenReturn(nonGameUsedCards)

        val result = cardService.findAllByGameUsedMaterial(false)

        assertEquals(nonGameUsedCards.size, result.size)
        assertTrue(result.all { !it.gameUsedMaterial })
        verify(cardRepository).findAllByGameUsedMaterial(false)
    }

    @Test
    fun `getCardsFiltered when no filters applied should return all cards`() {
        `when`(cardRepository.findAll()).thenReturn(sampleCards)

        val result = cardService.getCardsFiltered(null, null, null, null, null, null, null, null, null, null, null, null)

        assertEquals(sampleCards.size, result.size)
        verify(cardRepository).findAll()
    }

    @Test
    fun `getCardsFiltered when gameUsed is true should return only game used cards`() {
        `when`(cardRepository.findAll()).thenReturn(sampleCards)
        val expectedCards = sampleCards.filter { it.gameUsedMaterial }

        val result = cardService.getCardsFiltered(null, null, null, null, null, null, true, null, null, null, null, null)

        assertEquals(expectedCards.size, result.size)
        assertTrue(result.all { it.gameUsedMaterial })
    }

    @Test
    fun `getCardsFiltered when gameUsed is false should return only non-game used cards`() {
        `when`(cardRepository.findAll()).thenReturn(sampleCards)
        val expectedCards = sampleCards.filter { !it.gameUsedMaterial }

        val result = cardService.getCardsFiltered(null, null, null, null, null, null, false, null, null, null, null, null)

        assertEquals(expectedCards.size, result.size)
        assertTrue(result.all { !it.gameUsedMaterial })
    }

    @Test
    fun `getCardsFiltered when autograph is true should return only autographed cards`() {
        `when`(cardRepository.findAll()).thenReturn(sampleCards)
        val expectedCards = sampleCards.filter { it.autograph }

        val result = cardService.getCardsFiltered(null, null, null, null, null, null, null, true, null, null, null, null)

        assertEquals(expectedCards.size, result.size)
        assertTrue(result.all { it.autograph })
    }

    @Test
    fun `getCardsFiltered when autograph is false should return only non-autographed cards`() {
        `when`(cardRepository.findAll()).thenReturn(sampleCards)
        val expectedCards = sampleCards.filter { !it.autograph }

        val result = cardService.getCardsFiltered(null, null, null, null, null, null, null, false, null, null, null, null)

        assertEquals(expectedCards.size, result.size)
        assertTrue(result.all { !it.autograph })
    }

    @Test
    fun `getCardsFiltered when gameUsed is true AND autograph is true`() {
        `when`(cardRepository.findAll()).thenReturn(sampleCards)
        val expectedCards = sampleCards.filter { it.gameUsedMaterial && it.autograph }

        val result = cardService.getCardsFiltered(null, null, null, null, null, null, true, true, null, null, null, null)

        assertEquals(expectedCards.size, result.size)
        assertTrue(result.all { it.gameUsedMaterial && it.autograph })
    }

    @Test
    fun `getCardsFiltered when gameUsed is true AND autograph is false`() {
        `when`(cardRepository.findAll()).thenReturn(sampleCards)
        val expectedCards = sampleCards.filter { it.gameUsedMaterial && !it.autograph }

        val result = cardService.getCardsFiltered(null, null, null, null, null, null, true, false, null, null, null, null)

        assertEquals(expectedCards.size, result.size)
        assertTrue(result.all { it.gameUsedMaterial && !it.autograph })
    }
    
    @Test
    fun `getCardsFiltered when manufacturerId is set AND gameUsed is true`() {
        `when`(cardRepository.findAll()).thenReturn(sampleCards)
        val targetManufacturerId = manufacturer1.id
        val expectedCards = sampleCards.filter { it.theme.brand.manufacturer.id == targetManufacturerId && it.gameUsedMaterial }

        val result = cardService.getCardsFiltered(manufacturerId = targetManufacturerId, null, null, null, null, null, gameUsed = true, null, null, null, null, null)

        assertEquals(expectedCards.size, result.size)
        assertTrue(result.all { it.theme.brand.manufacturer.id == targetManufacturerId && it.gameUsedMaterial })
    }

    @Test
    fun `getCardsFiltered with season, autograph false, and playerId`() {
        `when`(cardRepository.findAll()).thenReturn(sampleCards)
        val targetSeason = "S13" // Updated to match new sample data
        val targetPlayerId = player2.id // Updated to match new sample data
        val expectedCards = sampleCards.filter { it.season == targetSeason && !it.autograph && it.player.id == targetPlayerId }

        val result = cardService.getCardsFiltered(null, null, null, null, playerId = targetPlayerId, season = targetSeason, null, autograph = false, null, null, null, null)
        
        assertEquals(expectedCards.size, result.size)
        assertTrue(result.all { it.season == targetSeason && !it.autograph && it.player.id == targetPlayerId })
    }
     @Test
    fun `getCardsFiltered with brandId`() {
        `when`(cardRepository.findAll()).thenReturn(sampleCards)
        val targetBrandId = brand1.id
        val expectedCards = sampleCards.filter { it.theme.brand.id == targetBrandId }

        val result = cardService.getCardsFiltered(null, brandId = targetBrandId, null, null, null, null, null, null, null, null, null, null)

        assertEquals(expectedCards.size, result.size)
        assertTrue(result.all { it.theme.brand.id == targetBrandId })
    }

    @Test
    fun `getCardsFiltered with themeId`() {
        `when`(cardRepository.findAll()).thenReturn(sampleCards)
        val targetThemeId = theme2.id // Updated to match new sample data
        val expectedCards = sampleCards.filter { it.theme.id == targetThemeId }

        val result = cardService.getCardsFiltered(null, null, themeId = targetThemeId, null, null, null, null, null, null, null, null, null)

        assertEquals(expectedCards.size, result.size)
        assertTrue(result.all { it.theme.id == targetThemeId })
    }

    @Test
    fun `getCardsFiltered with sportId`() {
        `when`(cardRepository.findAll()).thenReturn(sampleCards)
        val targetSportId = sport1.id
        val expectedCards = sampleCards.filter { it.player.sport.id == targetSportId }

        val result = cardService.getCardsFiltered(null, null, null, sportId = targetSportId, null, null, null, null, null, null, null, null)

        assertEquals(expectedCards.size, result.size)
        assertTrue(result.all { it.player.sport.id == targetSportId })
    }

    @Test
    fun `getAllVariants should return all variants`() {
        val mockVariants = listOf(variant1, variant2)
        `when`(variantRepository.findAll()).thenReturn(mockVariants)

        val result = cardService.getAllVariants()

        assertEquals(mockVariants, result)
        verify(variantRepository).findAll()
    }

    @Test
    fun `getCardsFiltered with variantId`() {
        `when`(cardRepository.findAll()).thenReturn(sampleCards)
        val targetVariantId = variant1.id
        val expectedCards = sampleCards.filter { it.variant.id == targetVariantId }

        val result = cardService.getCardsFiltered(null, null, null, null, null, null, null, null, variantId = targetVariantId, null)

        assertEquals(expectedCards.size, result.size)
        assertTrue(result.all { it.variant.id == targetVariantId })
    }

    @Test
    fun `getCardsFiltered with rookieCard true`() {
        `when`(cardRepository.findAll()).thenReturn(sampleCards)
        val expectedCards = sampleCards.filter { it.rookieCard }

        val result = cardService.getCardsFiltered(null, null, null, null, null, null, null, null, null, rookieCard = true)

        assertEquals(expectedCards.size, result.size)
        assertTrue(result.all { it.rookieCard })
    }

    @Test
    fun `getCardsFiltered with rookieCard false`() {
        `when`(cardRepository.findAll()).thenReturn(sampleCards)
        val expectedCards = sampleCards.filter { !it.rookieCard }

        val result = cardService.getCardsFiltered(null, null, null, null, null, null, null, null, null, rookieCard = false)

        assertEquals(expectedCards.size, result.size)
        assertTrue(result.all { !it.rookieCard })
    }

    @Test
    fun `getCardsFiltered with variantId and rookieCard true`() {
        `when`(cardRepository.findAll()).thenReturn(sampleCards)
        val targetVariantId = variant1.id
        val expectedCards = sampleCards.filter { it.variant.id == targetVariantId && it.rookieCard }

        val result = cardService.getCardsFiltered(null, null, null, null, null, null, null, null, variantId = targetVariantId, rookieCard = true, null, null)
        
        assertEquals(expectedCards.size, result.size)
        assertTrue(result.all { it.variant.id == targetVariantId && it.rookieCard })
    }
     @Test
    fun `getCardsFiltered with variantId and rookieCard false and gameUsed true`() {
        `when`(cardRepository.findAll()).thenReturn(sampleCards)
        val targetVariantId = variant1.id 
        val expectedCards = sampleCards.filter { it.variant.id == targetVariantId && !it.rookieCard && it.gameUsedMaterial }

        val result = cardService.getCardsFiltered(null, null, null, null, null, null, gameUsed = true, null, variantId = targetVariantId, rookieCard = false, null, null)
        
        assertEquals(expectedCards.size, result.size) 
        assertTrue(result.all { it.variant.id == targetVariantId && !it.rookieCard && it.gameUsedMaterial })
    }

    // Tests for PrintRunRange filtering
    @Test
    fun `getCardsFiltered with printRunRangeKey ONE`() {
        `when`(cardRepository.findAll()).thenReturn(sampleCards)
        val expectedCards = sampleCards.filter { it.printRun == 1 }
        val result = cardService.getCardsFiltered(null, null, null, null, null, null, null, null, null, null, "ONE", null)
        assertEquals(expectedCards.size, result.size)
        assertTrue(result.all { it.printRun == 1 })
    }

    @Test
    fun `getCardsFiltered with printRunRangeKey LE_10`() {
        `when`(cardRepository.findAll()).thenReturn(sampleCards)
        val expectedCards = sampleCards.filter { it.printRun > 0 && it.printRun <= 10 }
        val result = cardService.getCardsFiltered(null, null, null, null, null, null, null, null, null, null, "LE_10", null)
        assertEquals(expectedCards.size, result.size)
        assertTrue(result.all { it.printRun > 0 && it.printRun <= 10 })
    }

    @Test
    fun `getCardsFiltered with printRunRangeKey LE_50`() {
        `when`(cardRepository.findAll()).thenReturn(sampleCards)
        val expectedCards = sampleCards.filter { it.printRun > 0 && it.printRun <= 50 }
        val result = cardService.getCardsFiltered(null, null, null, null, null, null, null, null, null, null, "LE_50", null)
        assertEquals(expectedCards.size, result.size)
        assertTrue(result.all { it.printRun > 0 && it.printRun <= 50 })
    }

    @Test
    fun `getCardsFiltered with printRunRangeKey LE_100`() {
        `when`(cardRepository.findAll()).thenReturn(sampleCards)
        val expectedCards = sampleCards.filter { it.printRun > 0 && it.printRun <= 100 }
        val result = cardService.getCardsFiltered(null, null, null, null, null, null, null, null, null, null, "LE_100", null)
        assertEquals(expectedCards.size, result.size)
        assertTrue(result.all { it.printRun > 0 && it.printRun <= 100 })
    }

    @Test
    fun `getCardsFiltered with printRunRangeKey LE_500`() {
        `when`(cardRepository.findAll()).thenReturn(sampleCards)
        val expectedCards = sampleCards.filter { it.printRun > 0 && it.printRun <= 500 }
        val result = cardService.getCardsFiltered(null, null, null, null, null, null, null, null, null, null, "LE_500", null)
        assertEquals(expectedCards.size, result.size)
        assertTrue(result.all { it.printRun > 0 && it.printRun <= 500 })
    }
    
    @Test
    fun `getCardsFiltered with printRunRangeKey LE_1000`() {
        `when`(cardRepository.findAll()).thenReturn(sampleCards)
        val expectedCards = sampleCards.filter { it.printRun > 0 && it.printRun <= 1000 }
        val result = cardService.getCardsFiltered(null, null, null, null, null, null, null, null, null, null, "LE_1000", null)
        assertEquals(expectedCards.size, result.size)
        assertTrue(result.all { it.printRun > 0 && it.printRun <= 1000 })
    }

    @Test
    fun `getCardsFiltered with printRunRangeKey ALL_WITH_PRINT_RUN`() {
        `when`(cardRepository.findAll()).thenReturn(sampleCards)
        val expectedCards = sampleCards.filter { it.printRun > 0 }
        val result = cardService.getCardsFiltered(null, null, null, null, null, null, null, null, null, null, "ALL_WITH_PRINT_RUN", null)
        assertEquals(expectedCards.size, result.size)
        assertTrue(result.all { it.printRun > 0 })
    }

    @Test
    fun `getCardsFiltered with printRunRangeKey null`() {
        `when`(cardRepository.findAll()).thenReturn(sampleCards)
        val result = cardService.getCardsFiltered(null, null, null, null, null, null, null, null, null, null, null, null)
        assertEquals(sampleCards.size, result.size)
    }

    @Test
    fun `getCardsFiltered with rookieCard true AND printRunRangeKey LE_100`() {
        `when`(cardRepository.findAll()).thenReturn(sampleCards)
        val expectedCards = sampleCards.filter { it.rookieCard && (it.printRun > 0 && it.printRun <= 100) }
        val result = cardService.getCardsFiltered(null, null, null, null, null, null, null, null, null, rookieCard = true, "LE_100", null)
        assertEquals(expectedCards.size, result.size)
        assertTrue(result.all { it.rookieCard && (it.printRun > 0 && it.printRun <= 100) })
    }

    // Tests for Team filtering
    @Test
    fun `getAllTeams should return all teams from repository`() {
        val mockTeams = listOf(team1, team2)
        `when`(teamRepository.findAll()).thenReturn(mockTeams)

        val result = cardService.getAllTeams()

        assertEquals(mockTeams, result)
        verify(teamRepository).findAll()
    }

    @Test
    fun `getCardsFiltered with teamId`() {
        `when`(cardRepository.findAll()).thenReturn(sampleCards)
        val targetTeamId = team1.id!!
        val expectedCards = sampleCards.filter { it.player.team.id == targetTeamId }
        val result = cardService.getCardsFiltered(null, null, null, null, null, null, null, null, null, null, null, targetTeamId)
        assertEquals(expectedCards.size, result.size)
        assertTrue(result.all { it.player.team.id == targetTeamId })
    }

    @Test
    fun `getCardsFiltered with teamId null`() {
        `when`(cardRepository.findAll()).thenReturn(sampleCards)
        val result = cardService.getCardsFiltered(null, null, null, null, null, null, null, null, null, null, null, null)
        assertEquals(sampleCards.size, result.size) // No team filtering should be applied
    }

    @Test
    fun `getCardsFiltered with teamId AND rookieCard true`() {
        `when`(cardRepository.findAll()).thenReturn(sampleCards)
        val targetTeamId = team2.id!!
        val expectedCards = sampleCards.filter { it.player.team.id == targetTeamId && it.rookieCard }
        val result = cardService.getCardsFiltered(null, null, null, null, null, null, null, null, null, rookieCard = true, null, targetTeamId)
        assertEquals(expectedCards.size, result.size)
        assertTrue(result.all { it.player.team.id == targetTeamId && it.rookieCard })
    }

    // Tests for refactored getAllBrands
    @Test
    fun `getAllBrands should call repository with manufacturerId if provided`() {
        val testManufacturerId = 1L
        val mockBrands = listOf(brand1) // Assuming brand1 is linked to manufacturer1
        `when`(cardBrandRepository.findAllByManufacturerIdOrderByNameAsc(testManufacturerId)).thenReturn(mockBrands)

        val result = cardService.getAllBrands(testManufacturerId)

        assertEquals(mockBrands, result)
        verify(cardBrandRepository).findAllByManufacturerIdOrderByNameAsc(testManufacturerId)
        verify(cardBrandRepository, never()).findAllByOrderByNameAsc()
    }

    @Test
    fun `getAllBrands should call repository for all brands if manufacturerId is null`() {
        val mockAllBrands = listOf(brand1, brand2)
        `when`(cardBrandRepository.findAllByOrderByNameAsc()).thenReturn(mockAllBrands)

        val result = cardService.getAllBrands(null)

        assertEquals(mockAllBrands, result)
        verify(cardBrandRepository).findAllByOrderByNameAsc()
        verify(cardBrandRepository, never()).findAllByManufacturerIdOrderByNameAsc(anyLong())
    }

    // Tests for refactored getAllThemes
    @Test
    fun `getAllThemes should call repository with brandId if provided`() {
        val testBrandId = brand1.id!!
        val mockThemes = listOf(theme1) // Assuming theme1 is linked to brand1
        `when`(cardThemeRepository.findAllByBrandIdOrderByNameAsc(testBrandId)).thenReturn(mockThemes)

        val result = cardService.getAllThemes(brandId = testBrandId) // manufacturerId can be null

        assertEquals(mockThemes, result)
        verify(cardThemeRepository).findAllByBrandIdOrderByNameAsc(testBrandId)
        verify(cardThemeRepository, never()).findAllByBrandManufacturerIdOrderByNameAsc(anyLong())
        verify(cardThemeRepository, never()).findAllByOrderByNameAsc()
    }
    
    @Test
    fun `getAllThemes should call repository with brandId if both brandId and manufacturerId provided`() {
        val testBrandId = brand1.id!!
        val testManufacturerId = manufacturer1.id!! // brand1 belongs to manufacturer1
        val mockThemes = listOf(theme1) 
        `when`(cardThemeRepository.findAllByBrandIdOrderByNameAsc(testBrandId)).thenReturn(mockThemes)

        val result = cardService.getAllThemes(manufacturerId = testManufacturerId, brandId = testBrandId)

        assertEquals(mockThemes, result)
        verify(cardThemeRepository).findAllByBrandIdOrderByNameAsc(testBrandId)
        verify(cardThemeRepository, never()).findAllByBrandManufacturerIdOrderByNameAsc(anyLong())
        verify(cardThemeRepository, never()).findAllByOrderByNameAsc()
    }


    @Test
    fun `getAllThemes should call repository with manufacturerId if brandId is null`() {
        val testManufacturerId = manufacturer1.id!!
        val mockThemes = listOf(theme1, theme3) // Themes linked to manufacturer1 via brand1
        `when`(cardThemeRepository.findAllByBrandManufacturerIdOrderByNameAsc(testManufacturerId)).thenReturn(mockThemes)

        val result = cardService.getAllThemes(manufacturerId = testManufacturerId, brandId = null)

        assertEquals(mockThemes, result)
        verify(cardThemeRepository).findAllByBrandManufacturerIdOrderByNameAsc(testManufacturerId)
        verify(cardThemeRepository, never()).findAllByBrandIdOrderByNameAsc(anyLong())
        verify(cardThemeRepository, never()).findAllByOrderByNameAsc()
    }

    @Test
    fun `getAllThemes should call repository for all themes if no IDs provided`() {
        val mockAllThemes = listOf(theme1, theme2, theme3)
        `when`(cardThemeRepository.findAllByOrderByNameAsc()).thenReturn(mockAllThemes)

        val result = cardService.getAllThemes(null, null)

        assertEquals(mockAllThemes, result)
        verify(cardThemeRepository).findAllByOrderByNameAsc()
        verify(cardThemeRepository, never()).findAllByBrandIdOrderByNameAsc(anyLong())
        verify(cardThemeRepository, never()).findAllByBrandManufacturerIdOrderByNameAsc(anyLong())
    }
}
