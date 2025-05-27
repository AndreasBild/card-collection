package de.maulmann.cardcollection.repository


import de.maulmann.cardcollection.model.Card
//import de.maulmann.cardcollection.model.CardTheme // Unused import
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor // Add this import
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.EntityGraph // Import for @EntityGraph
import org.springframework.data.repository.query.QueryByExampleExecutor

interface CardRepository : JpaRepository<Card, Long>, QueryByExampleExecutor<Card>, JpaSpecificationExecutor<Card> { // Add JpaSpecificationExecutor
    fun findAllByRookieCard(rookieCard: Boolean): List<Card>
    fun findAllByPlayerId(id: Long): List<Card>
    //fun findAllById(id: Long): List<Card> // Consider if this is needed, JpaRepository has findById. This is an inherited method.
    fun findAllByAutograph(autograph: Boolean): List<Card>
    fun findAllByGameUsedMaterial(gameUsedMaterial: Boolean): List<Card>

    // Removed findAllByThemeBrandManufacturerId
    // Removed findAllByThemeBrandId
    // Removed findAllByThemeId

    @Query("SELECT c FROM Card c WHERE c.player.sport.id = :sportId")
    fun findAllByPlayerSportId(sportId: Long): List<Card>

    // Queries with JOIN FETCH for details
    @Query("SELECT c FROM Card c JOIN FETCH c.season JOIN FETCH c.player p JOIN FETCH p.team JOIN FETCH p.sport JOIN FETCH c.variant JOIN FETCH c.theme t JOIN FETCH t.brand b JOIN FETCH b.manufacturer")
    fun findAllWithDetails(): List<Card>

    @Query("SELECT c FROM Card c JOIN FETCH c.season JOIN FETCH c.player p JOIN FETCH p.team JOIN FETCH p.sport JOIN FETCH c.variant JOIN FETCH c.theme t JOIN FETCH t.brand b JOIN FETCH b.manufacturer WHERE c.rookieCard = :rookieCard")
    fun findAllByRookieCardWithDetails(rookieCard: Boolean): List<Card>

    @Query("SELECT c FROM Card c JOIN FETCH c.season JOIN FETCH c.player p JOIN FETCH p.team JOIN FETCH p.sport JOIN FETCH c.variant JOIN FETCH c.theme t JOIN FETCH t.brand b JOIN FETCH b.manufacturer WHERE p.id = :playerId")
    fun findAllByPlayerIdWithDetails(playerId: Long): List<Card>

    @Query("SELECT c FROM Card c JOIN FETCH c.season JOIN FETCH c.player p JOIN FETCH p.team JOIN FETCH p.sport JOIN FETCH c.variant JOIN FETCH c.theme t JOIN FETCH t.brand b JOIN FETCH b.manufacturer WHERE c.autograph = :autograph")
    fun findAllByAutographWithDetails(autograph: Boolean): List<Card>

    @Query("SELECT c FROM Card c JOIN FETCH c.season JOIN FETCH c.player p JOIN FETCH p.team JOIN FETCH p.sport JOIN FETCH c.variant JOIN FETCH c.theme t JOIN FETCH t.brand b JOIN FETCH b.manufacturer WHERE c.gameUsedMaterial = :gameUsedMaterial")
    fun findAllByGameUsedMaterialWithDetails(gameUsedMaterial: Boolean): List<Card>

    @Query("SELECT c FROM Card c JOIN FETCH c.season JOIN FETCH c.player p JOIN FETCH p.sport JOIN FETCH p.team ps JOIN FETCH c.variant JOIN FETCH c.theme t JOIN FETCH t.brand b JOIN FETCH b.manufacturer WHERE p.sport.id = :sportId")
    fun findAllByPlayerSportIdWithDetails(sportId: Long): List<Card>

    // This is the method that takes Specification and Pageable, ensuring it's correctly named and overridden
    @EntityGraph(attributePaths = ["player.team", "player.sport", "theme.brand.manufacturer", "season", "variant"])
    override fun findAll(spec: org.springframework.data.jpa.domain.Specification<Card>?, pageable: org.springframework.data.domain.Pageable): org.springframework.data.domain.Page<Card>
}
