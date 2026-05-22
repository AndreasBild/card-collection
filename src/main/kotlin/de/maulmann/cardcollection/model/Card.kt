package de.maulmann.cardcollection.model

import jakarta.persistence.*

@Entity
class Card(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @OneToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "grading_id", nullable = true)
    var grading: Grading? = null,

    var printRun: Int,
    var serialNumber: Int,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "season_id")
    var season: Season,
    var number: String,
    var rookieCard: Boolean,
    var gameUsedMaterial: Boolean,
    var autograph: Boolean,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id")
    var player: Player,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    var team: Team?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id")
    var variant: Variant,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_id")
    var theme: CardTheme
)
