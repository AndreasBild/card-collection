package de.maulmann.cardcollection.service

import de.maulmann.cardcollection.model.Card
import de.maulmann.cardcollection.model.CardManufacturer
import de.maulmann.cardcollection.repository.CardManufacturerRepository
import de.maulmann.cardcollection.repository.CustomCardQueriesRepository
import org.springframework.stereotype.Service

@Service
class CardManufacturerService(
    private val actualCardManufacturerRepository: CardManufacturerRepository,
    private val customCardQueriesRepository: CustomCardQueriesRepository
) {

    fun getCardsByManufacturerId(manufacturerId: Long): List<Card> {
        return customCardQueriesRepository.findByManufacturerIdWithDetails(manufacturerId)
    }

    fun getAllCardManufacturers(): List<CardManufacturer> {
        return actualCardManufacturerRepository.findAll()
    }

}



