package de.maulmann.cardcollection.model
import jakarta.persistence.*
import org.hibernate.annotations.BatchSize

@Entity
class Card(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @OneToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "grading_id", nullable = true)
    var grading: Grading? = null,
    val printRun: Int,
    val serialNumber: Int,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "season_id")
    val season: Season,
    val number: String,
    val rookieCard: Boolean,
    val gameUsedMaterial: Boolean,
    val autograph: Boolean,
    @ManyToMany
    @JoinTable(
        name = "card_player",
        joinColumns = [JoinColumn(name = "card_id")],
        inverseJoinColumns = [JoinColumn(name = "player_id")]
    )
    @BatchSize(size = 20)
    val players: Set<Player> = emptySet(),
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    val team: Team?,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id")
    val variant: Variant,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_id")
    val theme: CardTheme
) {
    val playerNames: String
    get() = players.joinToString(", ") { "${it.name} ${it.surname}" }
    val sportNames: String
    get() = players.mapNotNull { it.sport?.name }.distinct().joinToString(", ")
}
