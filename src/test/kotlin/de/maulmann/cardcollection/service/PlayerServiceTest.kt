package de.maulmann.cardcollection.service

import de.maulmann.cardcollection.model.Player
import de.maulmann.cardcollection.model.Sport
import de.maulmann.cardcollection.model.Team
import de.maulmann.cardcollection.repository.PlayerRepository
import de.maulmann.cardcollection.repository.SportRepository
import de.maulmann.cardcollection.repository.TeamRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
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

    private fun createMockTeam(id: Long, name: String): Team = Team(id = id, name = name)
    private fun createMockSport(id: Long, name: String): Sport = Sport(id = id, name = name)
    private fun createMockPlayer(id: Long, name: String, surname: String, team: Team, sport: Sport): Player =
        Player(id = id, name = name, surname = surname, team = team, sport = sport)

    @Test
    fun `testGetPlayers_returnsListOfPlayers`() {
        // GIVEN
        val sampleTeam = createMockTeam(1L, "Chicago Bulls")
        val sampleSport = createMockSport(1L, "Basketball")
        val mockPlayer1 = createMockPlayer(1L, "Michael", "Jordan", sampleTeam, sampleSport)
        val mockPlayer2 = createMockPlayer(2L, "Scottie", "Pippen", sampleTeam, sampleSport)
        val expectedPlayers = listOf(mockPlayer1, mockPlayer2)
        whenever(playerRepository.findAll()).thenReturn(expectedPlayers)

        // WHEN
        val result = playerService.getPlayers()

        // THEN
        assertThat(result).isEqualTo(expectedPlayers)
        verify(playerRepository).findAll()
    }

    @Test
    fun `testAddPlayer_validData_createsAndReturnsPlayer`() {
        // GIVEN
        val teamId = 1L
        val sportId = 1L
        val playerName = "Dennis"
        val playerSurname = "Rodman"
        val sampleTeam = createMockTeam(teamId, "Chicago Bulls")
        val sampleSport = createMockSport(sportId, "Basketball")

        whenever(teamRepository.findById(teamId)).thenReturn(Optional.of(sampleTeam))
        whenever(sportRepository.findById(sportId)).thenReturn(Optional.of(sampleSport))
        whenever(playerRepository.save(any<Player>())).thenAnswer { invocation ->
            invocation.getArgument(0) as Player
        }

        // WHEN
        val result = playerService.addPlayer(playerName, playerSurname, teamId, sportId)

        // THEN
        assertThat(result).isNotNull
        assertThat(result.name).isEqualTo(playerName)
        assertThat(result.surname).isEqualTo(playerSurname)
        assertThat(result.team).isEqualTo(sampleTeam)
        assertThat(result.sport).isEqualTo(sampleSport)
        verify(playerRepository).save(any<Player>())
    }

    @Test
    fun `testAddPlayer_teamNotFound_throwsException`() {
        // GIVEN
        val teamId = 1L
        val sportId = 1L
        whenever(teamRepository.findById(teamId)).thenReturn(Optional.empty())

        // WHEN & THEN
        val exception = assertThrows<Exception> {
            playerService.addPlayer("TestName", "TestSurname", teamId, sportId)
        }
        assertThat(exception.message).isEqualTo("Team not found")
        verify(teamRepository).findById(teamId)
        verify(sportRepository, never()).findById(any())
        verify(playerRepository, never()).save(any())
    }

    @Test
    fun `testAddPlayer_sportNotFound_throwsException`() {
        // GIVEN
        val teamId = 1L
        val sportId = 1L
        val sampleTeam = createMockTeam(teamId, "Chicago Bulls")
        whenever(teamRepository.findById(teamId)).thenReturn(Optional.of(sampleTeam))
        whenever(sportRepository.findById(sportId)).thenReturn(Optional.empty())

        // WHEN & THEN
        val exception = assertThrows<Exception> {
            playerService.addPlayer("TestName", "TestSurname", teamId, sportId)
        }
        assertThat(exception.message).isEqualTo("Sport not found")
        verify(teamRepository).findById(teamId)
        verify(sportRepository).findById(sportId)
        verify(playerRepository, never()).save(any())
    }
}
