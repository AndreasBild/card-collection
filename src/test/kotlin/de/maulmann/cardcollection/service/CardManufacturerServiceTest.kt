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

    // Helper function to create a mock Player
    private fun createMockPlayer(): Player = mock {
        on { id } doReturn 1L
        on { name } doReturn "Test"
        on { surname } doReturn "Player"
        on { team } doReturn mock<Team>()
        on { sport } doReturn mock<Sport>()
    }

    // Helper function to create a mock Theme
    private fun createMockTheme(): CardTheme = mock {
        on { id } doReturn 1L
        on { name } doReturn "Test Theme"
        on { brand } doReturn mock<CardBrand>()
    }

    // Helper function to create a mock Variant
    private fun createMockVariant(): Variant = mock { // Corrected CardVariant to Variant
        on { id } doReturn 1L
        on { name } doReturn "Test Variant"
    }
    
    // Helper function to create a mock Card
    private fun createMockCard(id: Long): Card = mock {
        on { this.id } doReturn id
        on { this.player } doReturn createMockPlayer()
        on { this.theme } doReturn createMockTheme()
        on { this.variant } doReturn createMockVariant() // Now calls the corrected createMockVariant
        on { this.number } doReturn "101"
        on { this.season } doReturn "2023-24"
        on { this.printRun } doReturn 100
        on { this.serialNumber } doReturn 10
        on { this.rookieCard } doReturn false
        on { this.gameUsedMaterial } doReturn false
        on { this.autograph } doReturn false
        // on { this.notes } doReturn "Mock card notes" // Removed as 'notes' is not a property of Card
    }

    @Test
    fun `testGetCardsByManufacturerId_returnsListOfCards`() {
        // GIVEN
        val manufacturerId = 1L
        val mockCard1 = createMockCard(1L)
        val mockCard2 = createMockCard(2L)
        val expectedCards = listOf(mockCard1, mockCard2)
        whenever(customCardQueriesRepository.findByManufacturerId(manufacturerId)).thenReturn(expectedCards)

        // WHEN
        val result = cardManufacturerService.getCardsByManufacturerId(manufacturerId)

        // THEN
        assertThat(result).isEqualTo(expectedCards)
        verify(customCardQueriesRepository).findByManufacturerId(manufacturerId)
    }

    @Test
    fun `testGetAllCardManufacturers_returnsListOfManufacturers`() {
        // GIVEN
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
