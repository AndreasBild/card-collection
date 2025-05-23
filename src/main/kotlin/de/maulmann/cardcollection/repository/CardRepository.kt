package de.maulmann.cardcollection.repository


import de.maulmann.cardcollection.model.Card
//import de.maulmann.cardcollection.model.CardTheme // Unused import
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor // Add this import
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.QueryByExampleExecutor

interface CardRepository : JpaRepository<Card, Long>, QueryByExampleExecutor<Card>, JpaSpecificationExecutor<Card> { // Add JpaSpecificationExecutor
    fun findAllByRookieCard(rookieCard: Boolean): List<Card>
    fun findAllByPrintRun(printRun: Int): List<Card>
    fun findAllByPrintRunLessThanEqual(printRunIsLessThan: Int): List<Card>
    fun findAllByPrintRunGreaterThan(value: Int): List<Card>
    fun findAllByPlayerId(id: Long): List<Card>
    fun findAllById(id: Long): List<Card> // Consider if this is needed, JpaRepository has findById
    fun findAllByAutograph(autograph: Boolean): List<Card>
    fun findAllByGameUsedMaterial(gameUsedMaterial: Boolean): List<Card>

    @Query("SELECT c FROM Card c WHERE c.theme.brand.manufacturer.id = :manufacturerId")
    fun findAllByThemeBrandManufacturerId(manufacturerId: Long): List<Card>

    @Query("SELECT c FROM Card c WHERE c.theme.brand.id = :brandId")
    fun findAllByThemeBrandId(brandId: Long): List<Card>

    fun findAllByThemeId(themeId: Long): List<Card>

    @Query("SELECT c FROM Card c WHERE c.player.sport.id = :sportId")
    fun findAllByPlayerSportId(sportId: Long): List<Card>

    fun findAllBySeason(season: String): List<Card>

    fun findAllByVariantId(variantId: Long): List<Card>

    @Query("SELECT DISTINCT c.season FROM Card c ORDER BY c.season ASC")
    fun findDistinctSeasons(): List<String>
}


