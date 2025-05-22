package de.maulmann.cardcollection.repository

import de.maulmann.cardcollection.model.CardManufacturer
import org.springframework.data.jpa.repository.JpaRepository

interface ActualCardManufacturerRepository : JpaRepository<CardManufacturer, Long>
