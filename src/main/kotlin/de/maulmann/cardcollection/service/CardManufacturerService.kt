package de.maulmann.cardcollection.service

import de.maulmann.cardcollection.model.Card
import de.maulmann.cardcollection.repository.CardManufacturerRepository
import org.springframework.stereotype.Service

@Service
class CardManufacturerService(private val cardManufacturerRepository: CardManufacturerRepository) {

    fun getCardsByManufacturerId(manufacturerId: Long): List<Card> {
        return cardManufacturerRepository.findByManufacturerId(manufacturerId)
    }

    fun getAllCardManufacturers(): MutableList<Card> {
        return cardManufacturerRepository.findAll()
    }

}



