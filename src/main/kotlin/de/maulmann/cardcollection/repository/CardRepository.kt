package de.maulmann.cardcollection.repository


import de.maulmann.cardcollection.model.Card
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.query.QueryByExampleExecutor

interface CardRepository : JpaRepository<Card, Long>,QueryByExampleExecutor<Card> {
    abstract fun findAllByRookieCard(rookieCard: Boolean): MutableList<Card>
    abstract fun findAllByPlayerId(id: Long): MutableList<Card>
    abstract fun findAllById(id: Long): MutableList<Card>


}


