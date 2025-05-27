package de.maulmann.cardcollection.service

import de.maulmann.cardcollection.model.* // Assuming this imports Player, Theme, Variant, Sport, Team as well
import de.maulmann.cardcollection.repository.ActualCardManufacturerRepository
import de.maulmann.cardcollection.repository.CustomCardQueriesRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.* // For whenever, verify, mock, etc.

@ExtendWith(MockitoExtension::class)
class CardManufacturerServiceTest {

    @Mock
    private lateinit var actualCardManufacturerRepository: ActualCardManufacturerRepository

    @Mock
    private lateinit var customCardQueriesRepository: CustomCardQueriesRepository

    @InjectMocks
    private lateinit var cardManufacturerService: CardManufacturerService

    // Pre-configured stable mock instances - further simplified
    private val stableMockPlayer: Player = mock { on { id } doReturn 1L } 
    private val mockTheme: CardTheme = mock { on { id } doReturn 1L }
    private val mockVariant: Variant = mock { on { id } doReturn 1L }
    private val mockSeason: Season = mock { on { id } doReturn 1L }

    private fun getMockPlayer(): Player = stableMockPlayer
    
    // Helper function to create a mock Card instance using highly simplified stable mocks
    private fun createMockCard(id: Long): Card {
        return mock {
            on { this.id } doReturn id // Essential for comparison
            // Stubbing only ID for related entities if other properties were flagged as unnecessary
            on { player } doReturn getMockPlayer()
            on { theme } doReturn mockTheme
            on { variant } doReturn mockVariant
            on { season } doReturn mockSeason
        }
    }

    @Test
    fun `testGetCardsByManufacturerId_returnsListOfCards`() {
        // GIVEN
        val manufacturerId = 1L
        val mockCard1 = createMockCard(1L) 
        val mockCard2 = createMockCard(2L) 
        val expectedCards = listOf(mockCard1, mockCard2)
        whenever(customCardQueriesRepository.findByManufacturerIdWithDetails(manufacturerId)).thenReturn(expectedCards)

        // WHEN
        val result = cardManufacturerService.getCardsByManufacturerId(manufacturerId)

        // THEN
        assertThat(result).isEqualTo(expectedCards)
        verify(customCardQueriesRepository).findByManufacturerIdWithDetails(manufacturerId)
    }

    @Test
    fun `testGetAllCardManufacturers_returnsListOfManufacturers`() {
        // GIVEN
        // CardManufacturer is a data class, 'name' is a primary constructor param, likely used in equals().
        // 'id' is also a primary constructor param.
        // If UnnecessaryStubbingException listed 'id', it implies equals might not have been called or only name mattered.
        // Let's keep 'name' as it's more descriptive and was NOT listed as unnecessary for the mock objects themselves.
        val mockManufacturer1: CardManufacturer = mock {
            on { id } doReturn 1L 
            on { name } doReturn "Panini" 
        }
        val mockManufacturer2: CardManufacturer = mock {
            on { id } doReturn 2L
            on { name } doReturn "Topps"
        }
        val expectedManufacturers = listOf(mockManufacturer1, mockManufacturer2)
        whenever(actualCardManufacturerRepository.findAll()).thenReturn(expectedManufacturers)

        // WHEN
        val result = cardManufacturerService.getAllCardManufacturers()

        // THEN
        assertThat(result).isEqualTo(expectedManufacturers)
        verify(actualCardManufacturerRepository).findAll()
    }
}
