package de.maulmann.cardcollection.service

import de.maulmann.cardcollection.model.*
import de.maulmann.cardcollection.repository.ActualCardManufacturerRepository
import de.maulmann.cardcollection.repository.CustomCardQueriesRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
class CardManufacturerServiceTest {

    @Mock
    private lateinit var actualCardManufacturerRepository: ActualCardManufacturerRepository

    @Mock
    private lateinit var customCardQueriesRepository: CustomCardQueriesRepository

    @InjectMocks
    private lateinit var cardManufacturerService: CardManufacturerService

    @Test
    fun `getCardsByManufacturerId should return cards from the repository`() {
        val manufacturerId = 1L
        val sport = Sport(name = "Basketball")
        val team = Team(name = "Lakers")
        val player = Player(name = "LeBron", surname = "James", sport = sport, team = team)
        val season = Season(name = "2022-23")
        val variant = Variant(name = "Silver Prizm")
        val manufacturer = CardManufacturer(name = "Panini")
        val brand = CardBrand(name = "Prizm", manufacturer = manufacturer)
        val theme = CardTheme(name = "Base Set", brand = brand)
        val cards = listOf(
            Card(number = "23", autograph = false, gameUsedMaterial = false, rookieCard = false, printRun = 100, serialNumber = 1, player = player, season = season, variant = variant, theme = theme)
        )
        whenever(customCardQueriesRepository.findByManufacturerIdWithDetails(manufacturerId)).thenReturn(cards)

        val result = cardManufacturerService.getCardsByManufacturerId(manufacturerId)

        assertEquals(1, result.size)
        assertEquals(cards, result)
        verify(customCardQueriesRepository).findByManufacturerIdWithDetails(manufacturerId)
    }

    @Test
    fun `getAllCardManufacturers should return all manufacturers from the repository`() {
        val manufacturers = listOf(CardManufacturer(name = "Manufacturer A"), CardManufacturer(name = "Manufacturer B"))
        whenever(actualCardManufacturerRepository.findAll()).thenReturn(manufacturers)

        val result = cardManufacturerService.getAllCardManufacturers()

        assertEquals(2, result.size)
        assertEquals(manufacturers, result)
        verify(actualCardManufacturerRepository).findAll()
    }
}
