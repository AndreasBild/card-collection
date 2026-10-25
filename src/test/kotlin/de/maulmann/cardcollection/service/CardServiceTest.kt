package de.maulmann.cardcollection.service

import de.maulmann.cardcollection.dto.CardFilter
import de.maulmann.cardcollection.model.*
import de.maulmann.cardcollection.repository.CardRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.Specification

class CardServiceTest {

    private lateinit var cardRepository: CardRepository
    private lateinit var cardService: CardService

    @BeforeEach
    fun setUp() {
        cardRepository = mock<CardRepository>()
        cardService = CardService(cardRepository)
    }

    @Test
    fun `getCardsFiltered calls repository with specification and pageable`() {
        val filter = CardFilter(
            manufacturerId = 1L,
            brandId = 2L,
            themeId = 3L,
            sportId = 4L,
            playerId = 5L,
            seasonId = 6L,
            gameUsed = true,
            autograph = true,
            variantId = 7L,
            rookieCard = true,
            printRunRangeKey = PrintRunRange.ONE.key,
            teamId = 8L,
            isGradedNullable = true
        )
        val pageable = PageRequest.of(0, 20)
        val emptyPage = PageImpl(emptyList<Card>(), pageable, 0)

        whenever(cardRepository.findAll(any<Specification<Card>>(), any<PageRequest>())).thenReturn(emptyPage)

        val result = cardService.getCardsFiltered(filter, pageable)
        assertThat(result).isNotNull
        assertThat(result.totalElements).isEqualTo(0)
    }
}
