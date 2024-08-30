package de.maulmann.cardcollection.service

import de.maulmann.cardcollection.model.Player
import de.maulmann.cardcollection.repository.PlayerRepository
import de.maulmann.cardcollection.repository.TeamRepository
import org.springframework.stereotype.Service

@Service
class PlayerService(
    private val playerRepository: PlayerRepository,
    private val teamRepository: TeamRepository
) {

    fun getPlayers(): List<Player> {
        return playerRepository.findAll()
    }

    fun addPlayer(name: String, surname: String, teamId: Long): Player {
        val team = teamRepository.findById(teamId).orElseThrow { Exception("Team not found") }
        val player = Player(name = name, surname = surname, team = team)
        return playerRepository.save(player)
    }
}