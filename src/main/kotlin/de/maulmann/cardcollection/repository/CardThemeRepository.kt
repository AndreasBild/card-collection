package de.maulmann.cardcollection.repository


import de.maulmann.cardcollection.model.CardTheme
import org.springframework.data.jpa.repository.JpaRepository

interface CardThemeRepository : JpaRepository<CardTheme, Long> {
    fun findAllByBrandIdOrderByNameAsc(brandId: Long): List<CardTheme>
    fun findAllByBrandManufacturerIdOrderByNameAsc(manufacturerId: Long): List<CardTheme>
    fun findAllByOrderByNameAsc(): List<CardTheme>
}
