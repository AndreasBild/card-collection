package de.maulmann.cardcollection.model
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
@Entity
class Player(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val name: String,
    val surname : String,
    @ManyToMany
    @JoinTable(
        name = "player_team",
        joinColumns = [JoinColumn(name = "player_id")],
        inverseJoinColumns = [JoinColumn(name = "team_id")]
    )
    @JsonIgnore
    val teams: Set<Team> = emptySet(),
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sport_id")
    @JsonIgnore
    val sport: Sport?,
)