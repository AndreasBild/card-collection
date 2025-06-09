package de.maulmann.cardcollection.model

import jakarta.persistence.*

@Entity
data class Player(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val name: String,
    val surname : String,

    @ManyToOne
    @JoinColumn(name = "team_id")
    val team: Team?,

    @ManyToOne
    @JoinColumn(name = "sport_id")
    val sport: Sport?,

)

