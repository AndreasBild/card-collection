package de.maulmann.cardcollection.repository


import de.maulmann.cardcollection.model.Card
import org.springframework.data.jpa.repository.JpaRepository

interface CardRepository : JpaRepository<Card, Long>
