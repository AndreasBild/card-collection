package de.maulmann.cardcollection.repository

import de.maulmann.cardcollection.model.Grading
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GradingRepository : JpaRepository<Grading, Long>
