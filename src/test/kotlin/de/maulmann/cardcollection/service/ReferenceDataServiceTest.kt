package de.maulmann.cardcollection.service

import de.maulmann.cardcollection.model.*
import de.maulmann.cardcollection.repository.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class ReferenceDataServiceTest {

    private lateinit var cardRepository: CardRepository
    private lateinit var cardBrandRepository: CardBrandRepository
    private lateinit var cardThemeRepository: CardThemeRepository
    private lateinit var sportRepository: SportRepository
    private lateinit var variantRepository: VariantRepository
    private lateinit var teamRepository: TeamRepository
    private lateinit var seasonRepository: SeasonRepository
    private lateinit var cardManufacturerRepository: CardManufacturerRepository
    private lateinit var referenceDataService: ReferenceDataService

    @BeforeEach
    fun setUp() {
        cardRepository = mock<CardRepository>()
        cardBrandRepository = mock<CardBrandRepository>()
        cardThemeRepository = mock<CardThemeRepository>()
        sportRepository = mock<SportRepository>()
        variantRepository = mock<VariantRepository>()
        teamRepository = mock<TeamRepository>()
        seasonRepository = mock<SeasonRepository>()
        cardManufacturerRepository = mock<CardManufacturerRepository>()

        referenceDataService = ReferenceDataService(
            cardRepository,
            cardBrandRepository,
            cardThemeRepository,
            sportRepository,
            variantRepository,
            teamRepository,
            seasonRepository,
            cardManufacturerRepository
        )
    }

    @Test
    fun `getAllBrands with and without manufacturerId`() {
        val brand = CardBrand(id = 1L, name = "Prizm")
        whenever(cardRepository.findDistinctBrandsByManufacturerId(1L)).thenReturn(listOf(brand))
        whenever(cardBrandRepository.findAllByOrderByNameAsc()).thenReturn(listOf(brand))

        val filteredBrands = referenceDataService.getAllBrands(1L)
        assertThat(filteredBrands).hasSize(1)

        val allBrands = referenceDataService.getAllBrands(null)
        assertThat(allBrands).hasSize(1)
    }

    @Test
    fun `getAllThemes with and without filter parameters`() {
        val theme = CardTheme(id = 1L, name = "Base")
        whenever(cardRepository.findDistinctThemesByManufacturerIdAndBrandId(1L, 2L)).thenReturn(listOf(theme))
        whenever(cardThemeRepository.findAllByOrderByNameAsc()).thenReturn(listOf(theme))

        val filteredThemes = referenceDataService.getAllThemes(1L, 2L)
        assertThat(filteredThemes).hasSize(1)

        val allThemes = referenceDataService.getAllThemes(null, null)
        assertThat(allThemes).hasSize(1)
    }

    @Test
    fun `getAllVariants with and without filter parameters`() {
        val variant = Variant(id = 1L, name = "Gold")
        whenever(cardRepository.findDistinctVariantsByFilter(1L, 2L, 3L)).thenReturn(listOf(variant))
        whenever(variantRepository.findAll()).thenReturn(listOf(variant))

        val filteredVariants = referenceDataService.getAllVariants(1L, 2L, 3L)
        assertThat(filteredVariants).hasSize(1)

        val allVariants = referenceDataService.getAllVariants(null, null, null)
        assertThat(allVariants).hasSize(1)
    }
}
