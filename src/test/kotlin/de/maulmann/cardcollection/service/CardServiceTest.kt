package de.maulmann.cardcollection.service

import de.maulmann.cardcollection.model.Card
import de.maulmann.cardcollection.model.CardBrand
import de.maulmann.cardcollection.repository.CardBrandRepository
import de.maulmann.cardcollection.repository.CardRepository
import de.maulmann.cardcollection.repository.CardThemeRepository
import de.maulmann.cardcollection.repository.SportRepository
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

    @InjectMocks
    private lateinit var cardService: CardService

    @Test
    fun `getAllBrands_shouldReturnListOfBrands`() {
        // Given
        val mockBrands = listOf(
            CardBrand(1, "Brand A", mock()),
            CardBrand(2, "Brand B", mock())
        )
        `when`(cardBrandRepository.findAll()).thenReturn(mockBrands)

        // When
        val result = cardService.getAllBrands()

        // Then
        assertEquals(mockBrands, result)
        verify(cardBrandRepository).findAll()
    }

    @Test
    fun `getCardsBySeason_shouldReturnFilteredCardList`() {
        // Given
        val season = "2023-24"
        val mockCards = listOf(
            Card(id = 1, season = season, player = mock(), theme = mock(), variant = mock(), number = "1", printRun = 100, serialNumber = 1, rookieCard = false, gameUsedMaterial = false, autograph = false),
            Card(id = 2, season = season, player = mock(), theme = mock(), variant = mock(), number = "2", printRun = 100, serialNumber = 1, rookieCard = false, gameUsedMaterial = false, autograph = false)
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
    private fun <T> mock(): T {
        return org.mockito.Mockito.mock(org.mockito.Mockito::class.java) as T
    }
     private inline fun <reified T> mock(): T = org.mockito.Mockito.mock(T::class.java)
}
