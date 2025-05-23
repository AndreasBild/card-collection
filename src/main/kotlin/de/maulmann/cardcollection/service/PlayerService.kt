package de.maulmann.cardcollection.service

import de.maulmann.cardcollection.model.Player
import de.maulmann.cardcollection.repository.PlayerRepository
import de.maulmann.cardcollection.repository.TeamRepository
import de.maulmann.cardcollection.repository.SportRepository
import org.springframework.stereotype.Service

@Service
class PlayerService(
    private val playerRepository: PlayerRepository,
    private val teamRepository: TeamRepository,
    private val sportRepository: SportRepository

) {

    fun getPlayers(): List<Player> {
        return playerRepository.findAll()
    }

    fun addPlayer(name: String, surname: String, teamId: Long, sportId: Long): Player {
        val team = teamRepository.findById(teamId).orElseThrow { Exception("Team not found") }
        val sport = sportRepository.findById(sportId).orElseThrow { Exception("Sport not found") }
        val player = Player(name = name, surname = surname, team = team, sport = sport)
        return playerRepository.save(player)
    }
}