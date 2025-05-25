package de.maulmann.cardcollection.repository


import org.springframework.data.jpa.repository.JpaRepository


import de.maulmann.cardcollection.model.Card
import org.springframework.data.jpa.repository.Query

interface CustomCardQueriesRepository : JpaRepository<Card, Long> {

    @Query("SELECT c FROM Card c JOIN c.variant v JOIN v.theme ct JOIN ct.brand cb JOIN cb.manufacturer cm WHERE cm.id = :manufacturerId")
    fun findByManufacturerId(manufacturerId: Long): List<Card>
}