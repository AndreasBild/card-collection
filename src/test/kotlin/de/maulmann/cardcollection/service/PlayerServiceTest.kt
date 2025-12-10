package de.maulmann.cardcollection.service

import de.maulmann.cardcollection.model.Player
import de.maulmann.cardcollection.model.Sport
import de.maulmann.cardcollection.model.Team
import de.maulmann.cardcollection.repository.PlayerRepository
import de.maulmann.cardcollection.repository.SportRepository
import de.maulmann.cardcollection.repository.TeamRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.capture
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.*

@ExtendWith(MockitoExtension::class)
class PlayerServiceTest {

    @Mock
    private lateinit var playerRepository: PlayerRepository

    @Mock
    private lateinit var teamRepository: TeamRepository

    @Mock
    private lateinit var sportRepository: SportRepository

    @InjectMocks
    private lateinit var playerService: PlayerService

    @Test
    fun `getPlayers should return a list of players`() {
        val sport = Sport(name = "Sport A")
        val team = Team(name = "Team A")
        val players = listOf(Player(name = "John", surname = "Doe", team = team, sport = sport))
        whenever(playerRepository.findAllWithTeamAndSportSorted()).thenReturn(players)

        val result = playerService.getPlayers()

        assertEquals(1, result.size)
        assertEquals("John", result[0].name)
        verify(playerRepository).findAllWithTeamAndSportSorted()
    }

    @Test
    fun `addPlayer should save and return a new player`() {
        val teamId = 1L
        val sportId = 1L
        val team = Team(id = teamId, name = "Team A")
        val sport = Sport(id = sportId, name = "Sport A")
        val playerToSave = Player(name = "Jane", surname = "Doe", team = team, sport = sport)
        val savedPlayer = Player(id = 1, name = "Jane", surname = "Doe", team = team, sport = sport)

        whenever(teamRepository.findById(teamId)).thenReturn(Optional.of(team))
        whenever(sportRepository.findById(sportId)).thenReturn(Optional.of(sport))
        whenever(playerRepository.save(any<Player>())).thenReturn(savedPlayer)

        val result = playerService.addPlayer("Jane", "Doe", teamId, sportId)

        assertEquals(savedPlayer.id, result.id)
        assertEquals("Jane", result.name)

        val playerCaptor = ArgumentCaptor.forClass(Player::class.java)
        verify(playerRepository).save(capture(playerCaptor))
        assertEquals("Jane", playerCaptor.value.name)
        assertEquals(team, playerCaptor.value.team)
        assertEquals(sport, playerCaptor.value.sport)
    }

    @Test
    fun `addPlayer should throw exception when team is not found`() {
        val teamId = 1L
        val sportId = 1L
        whenever(teamRepository.findById(teamId)).thenReturn(Optional.empty())

        val exception = assertThrows<Exception> {
            playerService.addPlayer("John", "Doe", teamId, sportId)
        }
        assertEquals("Team not found", exception.message)
    }

    @Test
    fun `addPlayer should throw exception when sport is not found`() {
        val teamId = 1L
        val sportId = 1L
        val team = Team(id = teamId, name = "Team A")
        whenever(teamRepository.findById(teamId)).thenReturn(Optional.of(team))
        whenever(sportRepository.findById(sportId)).thenReturn(Optional.empty())

        val exception = assertThrows<Exception> {
            playerService.addPlayer("John", "Doe", teamId, sportId)
        }
        assertEquals("Sport not found", exception.message)
    }
}
