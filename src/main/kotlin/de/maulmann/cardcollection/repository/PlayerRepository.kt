package de.maulmann.cardcollection.repository
import de.maulmann.cardcollection.model.Player
import org.springframework.data.jpa.repository.JpaRepository
interface PlayerRepository : JpaRepository<Player, Long> {
    fun findAllByOrderBySurnameAscNameAsc(): List<Player>
}