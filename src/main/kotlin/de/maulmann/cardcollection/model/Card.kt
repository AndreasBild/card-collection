package de.maulmann.cardcollection.model

import jakarta.persistence.*
import org.hibernate.annotations.BatchSize

@Entity
class Card(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @OneToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "grading_id", nullable = true)
    var grading: Grading? = null,

    val printRun: Int? = null,
    val serialNumber: Int,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "season_id")
    val season: Season,
    val number: String,
    val rookieCard: Boolean,
    val gameUsedMaterial: Boolean,
    val autograph: Boolean,

    @OneToMany(mappedBy = "card", cascade = [CascadeType.ALL], orphanRemoval = true)
    @BatchSize(size = 20)
    val cardPlayers: MutableSet<CardPlayer> = mutableSetOf(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manufacturer_id")
    val manufacturer: CardManufacturer,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    val brand: CardBrand,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id")
    val variant: Variant,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_id")
    val theme: CardTheme
) {
    val playerNames: String
        get() = cardPlayers.joinToString(", ") { "${it.player.name} ${it.player.surname}" }

    val sportNames: String
        get() = cardPlayers.mapNotNull { it.player.sport?.name }.distinct().joinToString(", ")

    val teamNames: String
        get() = cardPlayers.mapNotNull { it.team?.name }.distinct().joinToString(", ")
}