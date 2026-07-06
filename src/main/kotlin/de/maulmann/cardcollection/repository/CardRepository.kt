package de.maulmann.cardcollection.repository

import de.maulmann.cardcollection.model.Card
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.query.QueryByExampleExecutor

interface CardRepository : JpaRepository<Card, Long>, QueryByExampleExecutor<Card>, JpaSpecificationExecutor<Card> {

    @org.springframework.data.jpa.repository.Query("""
        SELECT c FROM Card c
        LEFT JOIN FETCH c.season
        LEFT JOIN FETCH c.player p
        LEFT JOIN FETCH p.sport
        LEFT JOIN FETCH c.team
        LEFT JOIN FETCH c.variant
        LEFT JOIN FETCH c.theme t
        LEFT JOIN FETCH t.brand b
        LEFT JOIN FETCH b.manufacturer
        LEFT JOIN FETCH c.grading
        WHERE c.season.id = :seasonId
    """)
    fun findAllBySeasonIdWithDetails(seasonId: Long): List<Card>
}
