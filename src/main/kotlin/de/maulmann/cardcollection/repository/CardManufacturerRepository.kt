package de.maulmann.cardcollection.repository

import de.maulmann.cardcollection.model.CardManufacturer
import org.springframework.data.jpa.repository.JpaRepository

interface CardManufacturerRepository : JpaRepository<CardManufacturer, Long>
