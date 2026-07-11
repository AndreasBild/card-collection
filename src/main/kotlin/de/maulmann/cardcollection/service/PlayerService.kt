package de.maulmann.cardcollection.service
import de.maulmann.cardcollection.model.Player
import de.maulmann.cardcollection.repository.PlayerRepository
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
@Service
class PlayerService(
    private val playerRepository: PlayerRepository
) {
    @Cacheable("players")
    fun getPlayers(): List<Player> {
        return playerRepository.findAllByOrderBySurnameAscNameAsc()
    }
}