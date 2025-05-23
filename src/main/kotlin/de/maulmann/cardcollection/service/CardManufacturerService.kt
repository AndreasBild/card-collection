package de.maulmann.cardcollection.service

import de.maulmann.cardcollection.model.CardManufacturer
import de.maulmann.cardcollection.repository.ActualCardManufacturerRepository
import org.springframework.stereotype.Service

@Service
class CardManufacturerService(
    private val actualCardManufacturerRepository: ActualCardManufacturerRepository,
) {

    fun getAllCardManufacturers(): List<CardManufacturer> {
        return actualCardManufacturerRepository.findAll()
    }

}



