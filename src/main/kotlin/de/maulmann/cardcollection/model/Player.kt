package de.maulmann.cardcollection.model
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
@Entity
class Player(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    var name: String,
    var surname : String,
    @ManyToMany
    @JoinTable(
        name = "player_team",
        joinColumns = [JoinColumn(name = "player_id")],
        inverseJoinColumns = [JoinColumn(name = "team_id")]
    )
    @JsonIgnore
    var teams: MutableSet<Team> = mutableSetOf(),
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sport_id")
    @JsonIgnore
    var sport: Sport?,
)