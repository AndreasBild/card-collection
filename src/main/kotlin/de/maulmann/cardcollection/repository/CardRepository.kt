package de.maulmann.cardcollection.repository

import de.maulmann.cardcollection.model.Card
import de.maulmann.cardcollection.model.CardBrand
import de.maulmann.cardcollection.model.CardTheme
import de.maulmann.cardcollection.model.Variant
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.query.Param

interface CardRepository : JpaRepository<Card, Long>, JpaSpecificationExecutor<Card> {

    @org.springframework.data.jpa.repository.Query("""
        SELECT DISTINCT c FROM Card c
        LEFT JOIN FETCH c.season
        LEFT JOIN FETCH c.cardPlayers cp
        LEFT JOIN FETCH cp.player p
        LEFT JOIN FETCH p.sport
        LEFT JOIN FETCH cp.team
        LEFT JOIN FETCH c.variant
        LEFT JOIN FETCH c.theme
        LEFT JOIN FETCH c.brand
        LEFT JOIN FETCH c.manufacturer
        LEFT JOIN FETCH c.grading
        WHERE c.season.id = :seasonId
    """)
    fun findAllBySeasonIdWithDetails(@Param("seasonId") seasonId: Long): List<Card>

    @org.springframework.data.jpa.repository.Query("""
        SELECT DISTINCT c FROM Card c
        LEFT JOIN FETCH c.season
        LEFT JOIN FETCH c.cardPlayers cp
        LEFT JOIN FETCH cp.player p
        LEFT JOIN FETCH p.sport
        LEFT JOIN FETCH cp.team
        LEFT JOIN FETCH c.variant
        LEFT JOIN FETCH c.theme
        LEFT JOIN FETCH c.brand
        LEFT JOIN FETCH c.manufacturer
        LEFT JOIN FETCH c.grading
        ORDER BY c.id ASC
    """)
    fun findAllWithDetails(): List<Card>


    @org.springframework.data.jpa.repository.Query("""
        SELECT DISTINCT c.brand FROM Card c 
        WHERE :manufacturerId IS NULL OR c.manufacturer.id = :manufacturerId
        ORDER BY c.brand.name ASC
    """)
    fun findDistinctBrandsByManufacturerId(@Param("manufacturerId") manufacturerId: Long?): List<CardBrand>

    @org.springframework.data.jpa.repository.Query("""
        SELECT DISTINCT c.theme FROM Card c 
        WHERE (:manufacturerId IS NULL OR c.manufacturer.id = :manufacturerId)
          AND (:brandId IS NULL OR c.brand.id = :brandId)
        ORDER BY c.theme.name ASC
    """)
    fun findDistinctThemesByManufacturerIdAndBrandId(
        @Param("manufacturerId") manufacturerId: Long?,
        @Param("brandId") brandId: Long?
    ): List<CardTheme>

    @org.springframework.data.jpa.repository.Query("""
        SELECT DISTINCT c.variant FROM Card c 
        WHERE (:manufacturerId IS NULL OR c.manufacturer.id = :manufacturerId)
          AND (:brandId IS NULL OR c.brand.id = :brandId)
          AND (:themeId IS NULL OR c.theme.id = :themeId)
        ORDER BY c.variant.name ASC
    """)
    fun findDistinctVariantsByFilter(
        @Param("manufacturerId") manufacturerId: Long?,
        @Param("brandId") brandId: Long?,
        @Param("themeId") themeId: Long?
    ): List<Variant>
}