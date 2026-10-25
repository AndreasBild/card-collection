package de.maulmann.cardcollection.service

import de.maulmann.cardcollection.dto.CardFilter
import de.maulmann.cardcollection.model.Card
import de.maulmann.cardcollection.repository.CardRepository
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CardService(
    private val cardRepository: CardRepository
) {

    @Cacheable("filteredCards", key = "{#filter, #pageable.pageNumber, #pageable.pageSize, #pageable.sort}")
    fun getCardsFiltered(
        filter: CardFilter,
        pageable: Pageable
    ): Page<Card> {
        val cardsPage = cardRepository.findAll(CardSpecifications.fromFilter(filter), pageable)
        // The page query cannot fetch-join the cardPlayers collection (pagination would happen in memory),
        // so initialise players, sports and teams for exactly this page with one explicit query.
        cardsPage.content.map { it.id }.chunked(PEOPLE_FETCH_CHUNK_SIZE).forEach { ids ->
            cardRepository.fetchPeopleByIds(ids)
        }
        return cardsPage
    }

    private companion object {
        const val PEOPLE_FETCH_CHUNK_SIZE = 1000
    }

}