package de.maulmann.cardcollection.service

import de.maulmann.cardcollection.model.*
import de.maulmann.cardcollection.repository.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.Specification

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

    @Mock
    private lateinit var seasonRepository: SeasonRepository

    @InjectMocks
    private lateinit var cardService: CardService

    @Test
    fun `getAllCards should return all cards`() {
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
        whenever(cardRepository.findAllWithDetails()).thenReturn(cards)

        val result = cardService.getAllCards()

        assertEquals(1, result.size)
        verify(cardRepository).findAllWithDetails()
    }

    @Test
    fun `getCardsFiltered should apply manufacturerId filter`() {
        val pageable = PageRequest.of(0, 20)
        val cardPage = PageImpl(emptyList<Card>())
        whenever(cardRepository.findAll(any<Specification<Card>>(), any<PageRequest>())).thenReturn(cardPage)

        cardService.getCardsFiltered(manufacturerId = 1L, brandId = null, themeId = null, sportId = null, playerId = null, seasonId = null, gameUsed = null, autograph = null, variantId = null, rookieCard = null, printRunRangeKey = null, teamId = null, isGradedNullable = null, pageable = pageable)

        verify(cardRepository).findAll(any<Specification<Card>>(), any<PageRequest>())
    }

    @Test
    fun `getAllBrands should return all brands when no manufacturerId is provided`() {
        val manufacturer = CardManufacturer(name = "Panini")
        val brands = listOf(CardBrand(name = "Brand A", manufacturer = manufacturer), CardBrand(name = "Brand B", manufacturer = manufacturer))
        whenever(cardBrandRepository.findAllByOrderByNameAsc()).thenReturn(brands)

        val result = cardService.getAllBrands()

        assertEquals(2, result.size)
        verify(cardBrandRepository).findAllByOrderByNameAsc()
    }

    @Test
    fun `getAllBrands should return filtered brands when manufacturerId is provided`() {
        val manufacturerId = 1L
        val manufacturer = CardManufacturer(name = "Panini")
        val brands = listOf(CardBrand(name = "Brand A", manufacturer = manufacturer))
        whenever(cardBrandRepository.findAllByManufacturerIdOrderByNameAsc(manufacturerId)).thenReturn(brands)

        val result = cardService.getAllBrands(manufacturerId)

        assertEquals(1, result.size)
        verify(cardBrandRepository).findAllByManufacturerIdOrderByNameAsc(manufacturerId)
    }
}
