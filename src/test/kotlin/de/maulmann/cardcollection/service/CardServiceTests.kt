package de.maulmann.cardcollection.service

import de.maulmann.cardcollection.model.*
import de.maulmann.cardcollection.repository.CardBrandRepository
import de.maulmann.cardcollection.repository.CardRepository
import de.maulmann.cardcollection.repository.CardThemeRepository
import de.maulmann.cardcollection.repository.SportRepository
import de.maulmann.cardcollection.repository.VariantRepository
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

    @InjectMocks
    private lateinit var cardService: CardService

    private lateinit var sampleCards: List<Card>

    // Helper entities (can be expanded or moved to a setup method if more complex)
    private val sport1 = Sport(1, "Basketball")
    private val team1 = Team(1, "Chicago Bulls")
    private val player1 = Player(1, "Michael", "Jordan", team1, sport1)
    private val player2 = Player(2, "Scottie", "Pippen", team1, sport1)

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


        sampleCards = listOf(
            Card(1, 100, 10, "1997-98", "1", rookieCard = false, gameUsedMaterial = true, autograph = true, player1, theme1, variant1),
            Card(2, 500, 0, "1997-98", "2", rookieCard = true, gameUsedMaterial = false, autograph = false, player2, theme1, variant2),
            Card(3, 25, 5, "2020-21", "P1", rookieCard = false, gameUsedMaterial = true, autograph = false, player1, theme3, variant1), // Game used, no auto
            Card(4, 99, 0, "2020-21", "RA-MJ", rookieCard = false, gameUsedMaterial = false, autograph = true, player1, theme2, variant2), // Auto, no game used
            Card(5, 10, 1, "1993-94", "1", rookieCard = true, gameUsedMaterial = true, autograph = true, player2, theme3,variant1) // GU and Auto
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

        val result = cardService.getCardsFiltered(null, null, null, null, null, null, null, null, null, null)

        assertEquals(sampleCards.size, result.size)
        verify(cardRepository).findAll()
    }

    @Test
    fun `getCardsFiltered when gameUsed is true should return only game used cards`() {
        `when`(cardRepository.findAll()).thenReturn(sampleCards)
        val expectedCards = sampleCards.filter { it.gameUsedMaterial }

        val result = cardService.getCardsFiltered(null, null, null, null, null, null, true, null, null, null)

        assertEquals(expectedCards.size, result.size)
        assertTrue(result.all { it.gameUsedMaterial })
    }

    @Test
    fun `getCardsFiltered when gameUsed is false should return only non-game used cards`() {
        `when`(cardRepository.findAll()).thenReturn(sampleCards)
        val expectedCards = sampleCards.filter { !it.gameUsedMaterial }

        val result = cardService.getCardsFiltered(null, null, null, null, null, null, false, null, null, null)

        assertEquals(expectedCards.size, result.size)
        assertTrue(result.all { !it.gameUsedMaterial })
    }

    @Test
    fun `getCardsFiltered when autograph is true should return only autographed cards`() {
        `when`(cardRepository.findAll()).thenReturn(sampleCards)
        val expectedCards = sampleCards.filter { it.autograph }

        val result = cardService.getCardsFiltered(null, null, null, null, null, null, null, true, null, null)

        assertEquals(expectedCards.size, result.size)
        assertTrue(result.all { it.autograph })
    }

    @Test
    fun `getCardsFiltered when autograph is false should return only non-autographed cards`() {
        `when`(cardRepository.findAll()).thenReturn(sampleCards)
        val expectedCards = sampleCards.filter { !it.autograph }

        val result = cardService.getCardsFiltered(null, null, null, null, null, null, null, false, null, null)

        assertEquals(expectedCards.size, result.size)
        assertTrue(result.all { !it.autograph })
    }

    @Test
    fun `getCardsFiltered when gameUsed is true AND autograph is true`() {
        `when`(cardRepository.findAll()).thenReturn(sampleCards)
        val expectedCards = sampleCards.filter { it.gameUsedMaterial && it.autograph }

        val result = cardService.getCardsFiltered(null, null, null, null, null, null, true, true, null, null)

        assertEquals(expectedCards.size, result.size)
        assertTrue(result.all { it.gameUsedMaterial && it.autograph })
    }

    @Test
    fun `getCardsFiltered when gameUsed is true AND autograph is false`() {
        `when`(cardRepository.findAll()).thenReturn(sampleCards)
        val expectedCards = sampleCards.filter { it.gameUsedMaterial && !it.autograph }

        val result = cardService.getCardsFiltered(null, null, null, null, null, null, true, false, null, null)

        assertEquals(expectedCards.size, result.size)
        assertTrue(result.all { it.gameUsedMaterial && !it.autograph })
    }
    
    @Test
    fun `getCardsFiltered when manufacturerId is set AND gameUsed is true`() {
        `when`(cardRepository.findAll()).thenReturn(sampleCards)
        val targetManufacturerId = manufacturer1.id
        val expectedCards = sampleCards.filter { it.theme.brand.manufacturer.id == targetManufacturerId && it.gameUsedMaterial }

        val result = cardService.getCardsFiltered(manufacturerId = targetManufacturerId, null, null, null, null, null, gameUsed = true, null, null, null)

        assertEquals(expectedCards.size, result.size)
        assertTrue(result.all { it.theme.brand.manufacturer.id == targetManufacturerId && it.gameUsedMaterial })
    }

    @Test
    fun `getCardsFiltered with season, autograph false, and playerId`() {
        `when`(cardRepository.findAll()).thenReturn(sampleCards)
        val targetSeason = "1997-98"
        val targetPlayerId = player2.id
        val expectedCards = sampleCards.filter { it.season == targetSeason && !it.autograph && it.player.id == targetPlayerId }

        val result = cardService.getCardsFiltered(null, null, null, null, playerId = targetPlayerId, season = targetSeason, null, autograph = false, null, null)
        
        assertEquals(expectedCards.size, result.size)
        assertTrue(result.all { it.season == targetSeason && !it.autograph && it.player.id == targetPlayerId })
    }
     @Test
    fun `getCardsFiltered with brandId`() {
        `when`(cardRepository.findAll()).thenReturn(sampleCards)
        val targetBrandId = brand1.id
        val expectedCards = sampleCards.filter { it.theme.brand.id == targetBrandId }

        val result = cardService.getCardsFiltered(null, brandId = targetBrandId, null, null, null, null, null, null, null, null)

        assertEquals(expectedCards.size, result.size)
        assertTrue(result.all { it.theme.brand.id == targetBrandId })
    }

    @Test
    fun `getCardsFiltered with themeId`() {
        `when`(cardRepository.findAll()).thenReturn(sampleCards)
        val targetThemeId = theme2.id
        val expectedCards = sampleCards.filter { it.theme.id == targetThemeId }

        val result = cardService.getCardsFiltered(null, null, themeId = targetThemeId, null, null, null, null, null, null, null)

        assertEquals(expectedCards.size, result.size)
        assertTrue(result.all { it.theme.id == targetThemeId })
    }

    @Test
    fun `getCardsFiltered with sportId`() {
        `when`(cardRepository.findAll()).thenReturn(sampleCards)
        val targetSportId = sport1.id
        val expectedCards = sampleCards.filter { it.player.sport.id == targetSportId }

        val result = cardService.getCardsFiltered(null, null, null, sportId = targetSportId, null, null, null, null, null, null)

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
        // sampleCards[4] is: Card(5, 10, 1, "1993-94", "1", rookieCard = true, gameUsedMaterial = true, autograph = true, player2, theme3,variant1)
        // This is the only card that is variant1 and rookieCard true from the sample data.

        val result = cardService.getCardsFiltered(null, null, null, null, null, null, null, null, variantId = targetVariantId, rookieCard = true)
        
        assertEquals(expectedCards.size, result.size) // Should be 1
        assertTrue(result.all { it.variant.id == targetVariantId && it.rookieCard })
    }
     @Test
    fun `getCardsFiltered with variantId and rookieCard false and gameUsed true`() {
        `when`(cardRepository.findAll()).thenReturn(sampleCards)
        val targetVariantId = variant1.id // variant1 is used by card1, card3, card5
        // card1: rookie=false, gameUsed=true, variant=variant1
        // card3: rookie=false, gameUsed=true, variant=variant1
        // card5: rookie=true, gameUsed=true, variant=variant1
        val expectedCards = sampleCards.filter { it.variant.id == targetVariantId && !it.rookieCard && it.gameUsedMaterial }

        val result = cardService.getCardsFiltered(null, null, null, null, null, null, gameUsed = true, null, variantId = targetVariantId, rookieCard = false)
        
        assertEquals(expectedCards.size, result.size) // card1 and card3
        assertTrue(result.all { it.variant.id == targetVariantId && !it.rookieCard && it.gameUsedMaterial })
    }
}
