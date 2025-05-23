package de.maulmann.cardcollection.service

import de.maulmann.cardcollection.model.Card
import de.maulmann.cardcollection.model.CardBrand
import de.maulmann.cardcollection.repository.CardBrandRepository
import de.maulmann.cardcollection.repository.CardRepository
import de.maulmann.cardcollection.repository.CardThemeRepository
import de.maulmann.cardcollection.repository.SportRepository
import de.maulmann.cardcollection.repository.VariantRepository
import de.maulmann.cardcollection.repository.TeamRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class CardServiceTest {

    @Mock
    private lateinit var cardRepository: CardRepository

    @Mock
    private lateinit var cardBrandRepository: CardBrandRepository

    @Mock
    private lateinit var cardThemeRepository: CardThemeRepository // Mocked even if not used in these specific tests

    @Mock
    private lateinit var sportRepository: SportRepository // Mocked even if not used in these specific tests

    @Mock
    private lateinit var variantRepository: VariantRepository

    @Mock
    private lateinit var teamRepository: TeamRepository

    @InjectMocks
    private lateinit var cardService: CardService

    @Test
    fun `getAllBrands_shouldReturnListOfBrands`() {
        // Given
        // Using explicit Mockito.mock call to avoid potential issues with reified generic helper
        
        // Create plain mocks for CardBrand without stubbing their properties,
        // as these stubbings were reported as unnecessary.
        val mockBrand1 = org.mockito.Mockito.mock(CardBrand::class.java)
        val mockBrand2 = org.mockito.Mockito.mock(CardBrand::class.java)

        val newMockBrandsList = listOf(mockBrand1, mockBrand2)
        
        `when`(cardBrandRepository.findAllByOrderByNameAsc()).thenReturn(newMockBrandsList)

        // When
        val result = cardService.getAllBrands()

        // Then
        assertEquals(newMockBrandsList, result)
        verify(cardBrandRepository).findAllByOrderByNameAsc()
    }

    @Test
    fun `getCardsBySeason_shouldReturnFilteredCardList`() {
        // Given
        val season = "2023-24"
        // Using explicit Mockito.mock calls for clarity and to avoid potential helper issues
        val mockPlayer = org.mockito.Mockito.mock(de.maulmann.cardcollection.model.Player::class.java)
        // Removed unnecessary stubbings for mockPlayer.sport and mockPlayer.team
        // val mockSportForPlayer = org.mockito.Mockito.mock(de.maulmann.cardcollection.model.Sport::class.java) // Unused
        // val mockTeamForPlayer = org.mockito.Mockito.mock(de.maulmann.cardcollection.model.Team::class.java) // Unused
        // `when`(mockPlayer.sport).thenReturn(mockSportForPlayer) // Unnecessary
        // `when`(mockPlayer.team).thenReturn(mockTeamForPlayer) // Unnecessary

        val mockTheme = org.mockito.Mockito.mock(de.maulmann.cardcollection.model.CardTheme::class.java)
        val mockVariant = org.mockito.Mockito.mock(de.maulmann.cardcollection.model.Variant::class.java)

        val mockCards = listOf(
            Card(id = 1, season = season, player = mockPlayer, theme = mockTheme, variant = mockVariant, number = "1", printRun = 100, serialNumber = 1, rookieCard = false, gameUsedMaterial = false, autograph = false),
            Card(id = 2, season = season, player = mockPlayer, theme = mockTheme, variant = mockVariant, number = "2", printRun = 100, serialNumber = 1, rookieCard = true, gameUsedMaterial = true, autograph = false)
        )
        `when`(cardRepository.findAllBySeason(season)).thenReturn(mockCards)

        // When
        val result = cardService.getCardsBySeason(season)

        // Then
        assertEquals(mockCards, result)
        verify(cardRepository).findAllBySeason(season)
    }

    @Test
    fun `getAllSeasons_shouldReturnListOfSeasons`() {
        // Given
        val mockSeasons = listOf("2022-23", "2023-24")
        `when`(cardRepository.findDistinctSeasons()).thenReturn(mockSeasons)

        // When
        val result = cardService.getAllSeasons()

        // Then
        assertEquals(mockSeasons, result)
        verify(cardRepository).findDistinctSeasons()
    }

    // Helper to create mocks for complex objects, not strictly necessary for all Card fields but good practice
     private inline fun <reified T> mock(): T = org.mockito.Mockito.mock(T::class.java)
}
