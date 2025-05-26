package de.maulmann.cardcollection.model

import jakarta.persistence.*

@Entity
data class Card(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val printRun: Int,
    val serialNumber: Int,

    @ManyToOne
    @JoinColumn(name = "season_id")
    val season: Season,
    val number: String,
    val rookieCard: Boolean,
    val gameUsedMaterial: Boolean,
    val autograph: Boolean,

    @ManyToOne
    @JoinColumn(name = "player_id")
    val player: Player,

    @ManyToOne
    @JoinColumn(name = "variant_id")
    val variant: Variant,

    @ManyToOne
    @JoinColumn(name = "theme_id")
    val theme: CardTheme
)
