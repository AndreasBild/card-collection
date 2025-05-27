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

    // Removed all helper fields (stableMockPlayer, mockTheme, etc.) and getMockPlayer()
    // Removed mockTeam, mockSport, mockManufacturer, mockBrand as they are no longer used.

    // Helper function to create a mock Card instance - further simplified, no stubs
    private fun createMockCard(id: Long): Card { // id parameter might be unused now, but let's keep for structure
        return mock() 
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
        // Removed stubs for id and name as per instruction
        val mockManufacturer1: CardManufacturer = mock()
        val mockManufacturer2: CardManufacturer = mock()
        
        val expectedManufacturers = listOf(mockManufacturer1, mockManufacturer2)
        whenever(actualCardManufacturerRepository.findAll()).thenReturn(expectedManufacturers)

        // WHEN
        val result = cardManufacturerService.getAllCardManufacturers()

        // THEN
        assertThat(result).isEqualTo(expectedManufacturers)
        verify(actualCardManufacturerRepository).findAll()
    }
}
