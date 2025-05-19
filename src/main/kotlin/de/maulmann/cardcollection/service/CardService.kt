package de.maulmann.cardcollection.service

import de.maulmann.cardcollection.model.Card
import de.maulmann.cardcollection.repository.CardRepository
import org.springframework.stereotype.Service

@Service
class CardService(private val cardRepository: CardRepository) {

    fun getAllCards(): List<Card> = cardRepository.findAll()
    fun findAllByRookieCard(rookieCard: Boolean): MutableList<Card> {
        return cardRepository.findAllByRookieCard(rookieCard)
    }

    fun findAllByPlayerId(id: Long): MutableList<Card> {
        return cardRepository.findAllByPlayerId(id)

    }
    fun findAllByRookieCardId(id: Long): MutableList<Card> {
        return cardRepository.findAllByPlayerId(id)
    }

    fun findallById(id: Long): MutableList<Card> {
        return cardRepository.findAllById(id)
    }

    fun findAllByPrintRunIsLessThan(printRunIsLessThan: Int): MutableList<Card> {
        return cardRepository.findAllByPrintRunLessThanEqual(printRunIsLessThan)
    }
    fun findAllByAutograph(autograph: Boolean): MutableList<Card> {
        return cardRepository.findAllByAutograph(autograph)
    }

    fun getCardById(id: Long): Card? = cardRepository.findById(id).orElse(null)


}

