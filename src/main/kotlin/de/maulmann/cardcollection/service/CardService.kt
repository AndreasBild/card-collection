package de.maulmann.cardcollection.service

import de.maulmann.cardcollection.model.Card
import de.maulmann.cardcollection.repository.CardRepository
import org.springframework.stereotype.Service

@Service
class CardService(private val cardRepository: CardRepository) {

    fun getAllCards(): List<Card> = cardRepository.findAll()

    fun getCardById(id: Long): Card? = cardRepository.findById(id).orElse(null)
}

