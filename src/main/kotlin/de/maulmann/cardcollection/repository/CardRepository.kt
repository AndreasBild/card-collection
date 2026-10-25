package de.maulmann.cardcollection.repository

import de.maulmann.cardcollection.model.Card
import de.maulmann.cardcollection.model.CardBrand
import de.maulmann.cardcollection.model.CardTheme
import de.maulmann.cardcollection.model.Variant
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.query.Param

/** Shared fetch graph so every export query initialises exactly the same associations. */
private const val CARD_DETAILS_SELECT = """
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
"""

interface CardRepository : JpaRepository<Card, Long>, JpaSpecificationExecutor<Card> {

    @org.springframework.data.jpa.repository.Query(CARD_DETAILS_SELECT + " ORDER BY c.id ASC")
    fun findAllWithDetails(): List<Card>

    /** Graded cards that carry a certificate number; filtered in SQL instead of loading the whole collection. */
    @org.springframework.data.jpa.repository.Query(
        CARD_DETAILS_SELECT + """
        WHERE c.grading IS NOT NULL
          AND c.gradingCertNumber IS NOT NULL
          AND TRIM(c.gradingCertNumber) <> ''
        ORDER BY c.id ASC
        """
    )
    fun findAllGradedWithDetails(): List<Card>


    /**
     * Initialises the player/sport/team graph of already-loaded cards in one query. Intended to run after a
     * paged query within the same persistence context: the fetch join fills the managed cards' collections.
     */
    @org.springframework.data.jpa.repository.Query("""
        SELECT DISTINCT c FROM Card c
        LEFT JOIN FETCH c.cardPlayers cp
        LEFT JOIN FETCH cp.player p
        LEFT JOIN FETCH p.sport
        LEFT JOIN FETCH cp.team
        WHERE c.id IN :ids
    """)
    fun fetchPeopleByIds(@Param("ids") ids: Collection<Long>): List<Card>

    // Facet lookups select from the small reference table and probe `card` with EXISTS, so no DISTINCT over a
    // card join is needed and the composite card indexes serve the correlated probe.
    @org.springframework.data.jpa.repository.Query("""
        SELECT b FROM CardBrand b
        WHERE EXISTS (
            SELECT 1 FROM Card c
            WHERE c.brand = b
              AND (:manufacturerId IS NULL OR c.manufacturer.id = :manufacturerId)
        )
        ORDER BY b.name ASC
    """)
    fun findDistinctBrandsByManufacturerId(@Param("manufacturerId") manufacturerId: Long?): List<CardBrand>

    @org.springframework.data.jpa.repository.Query("""
        SELECT t FROM CardTheme t
        WHERE EXISTS (
            SELECT 1 FROM Card c
            WHERE c.theme = t
              AND (:manufacturerId IS NULL OR c.manufacturer.id = :manufacturerId)
              AND (:brandId IS NULL OR c.brand.id = :brandId)
        )
        ORDER BY t.name ASC
    """)
    fun findDistinctThemesByManufacturerIdAndBrandId(
        @Param("manufacturerId") manufacturerId: Long?,
        @Param("brandId") brandId: Long?
    ): List<CardTheme>

    @org.springframework.data.jpa.repository.Query("""
        SELECT v FROM Variant v
        WHERE EXISTS (
            SELECT 1 FROM Card c
            WHERE c.variant = v
              AND (:manufacturerId IS NULL OR c.manufacturer.id = :manufacturerId)
              AND (:brandId IS NULL OR c.brand.id = :brandId)
              AND (:themeId IS NULL OR c.theme.id = :themeId)
        )
        ORDER BY v.name ASC
    """)
    fun findDistinctVariantsByFilter(
        @Param("manufacturerId") manufacturerId: Long?,
        @Param("brandId") brandId: Long?,
        @Param("themeId") themeId: Long?
    ): List<Variant>
}