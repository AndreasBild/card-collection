package de.maulmann.cardcollection.model

import jakarta.persistence.*

@Entity
data class Player(
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
    val teams: Set<Team> = emptySet(),

    @ManyToOne
    @JoinColumn(name = "sport_id")
    val sport: Sport?,

)
