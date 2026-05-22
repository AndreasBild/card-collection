package de.maulmann.cardcollection.repository

import de.maulmann.cardcollection.model.Card
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.query.QueryByExampleExecutor

interface CardRepository : JpaRepository<Card, Long>, QueryByExampleExecutor<Card>, JpaSpecificationExecutor<Card>
