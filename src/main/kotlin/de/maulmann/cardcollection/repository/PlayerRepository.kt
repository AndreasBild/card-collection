package de.maulmann.cardcollection.repository
import de.maulmann.cardcollection.model.Player
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
interface PlayerRepository : JpaRepository<Player, Long> {
    @Query("SELECT p FROM Player p ORDER BY p.surname, p.name")
    fun findAllWithTeamAndSportSorted(): List<Player>
}