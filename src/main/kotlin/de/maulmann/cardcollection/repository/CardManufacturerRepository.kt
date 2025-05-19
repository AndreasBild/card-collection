package de.maulmann.cardcollection.repository


import de.maulmann.cardcollection.model.CardManufacturer
import org.springframework.data.jpa.repository.JpaRepository

//interface CardManufacturerRepository : JpaRepository<CardManufacturer, Long>


import de.maulmann.cardcollection.model.Card
import org.springframework.data.jpa.repository.Query

interface CardManufacturerRepository : JpaRepository<Card, Long> {

    @Query("SELECT c FROM Card c JOIN c.theme ct JOIN ct.brand cb JOIN cb.manufacturer cm WHERE cm.id = :manufacturerId")
    fun findByManufacturerId(manufacturerId: Long): List<Card>
}