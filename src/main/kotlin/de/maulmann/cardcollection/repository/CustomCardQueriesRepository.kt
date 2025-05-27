package de.maulmann.cardcollection.repository


import org.springframework.data.jpa.repository.JpaRepository


import de.maulmann.cardcollection.model.Card
import org.springframework.data.jpa.repository.Query

interface CustomCardQueriesRepository : JpaRepository<Card, Long> {

    @Query("SELECT c FROM Card c JOIN FETCH c.player JOIN FETCH c.theme ct JOIN FETCH ct.brand cb JOIN FETCH cb.manufacturer cm JOIN FETCH c.season JOIN FETCH c.variant WHERE cm.id = :manufacturerId")
    fun findByManufacturerIdWithDetails(manufacturerId: Long): List<Card>
}