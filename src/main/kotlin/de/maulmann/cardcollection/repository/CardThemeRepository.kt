package de.maulmann.cardcollection.repository

import de.maulmann.cardcollection.model.CardTheme
import org.springframework.data.jpa.repository.JpaRepository

interface CardThemeRepository : JpaRepository<CardTheme, Long> {
    fun findAllByOrderByNameAsc(): List<CardTheme>
}
