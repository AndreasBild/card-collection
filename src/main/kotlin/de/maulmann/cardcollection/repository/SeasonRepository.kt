package de.maulmann.cardcollection.repository

import de.maulmann.cardcollection.model.Season
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository // Optional, but good practice
interface SeasonRepository : JpaRepository<Season, Long> {
    fun findAllByOrderByNameAsc(): List<Season>
    // You can add custom query methods here if needed later, e.g.:
    // fun findByName(name: String): Season?
}
