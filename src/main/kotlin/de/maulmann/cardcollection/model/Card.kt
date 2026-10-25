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

    var gradingCertNumber: String? = null,

    var printRun: Int? = null,
    var packOdds: String? = null,
    var serialNumber: Int,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "season_id")
    var season: Season,
    var number: String,
    var rookieCard: Boolean,
    var gameUsedMaterial: Boolean,
    var autograph: Boolean,

    @OneToMany(mappedBy = "card", cascade = [CascadeType.ALL], orphanRemoval = true)
    @BatchSize(size = 100)
    var cardPlayers: MutableSet<CardPlayer> = mutableSetOf(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manufacturer_id")
    var manufacturer: CardManufacturer,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    var brand: CardBrand,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id")
    var variant: Variant,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_id")
    var theme: CardTheme
) {
    private val sortedCardPlayers: List<CardPlayer>
        get() {
            if (cardPlayers.size <= 1) return cardPlayers.toList()
            val juwan = ArrayList<CardPlayer>(1)
            val others = ArrayList<CardPlayer>(cardPlayers.size)
            for (cp in cardPlayers) {
                val fullName = "${cp.player.name} ${cp.player.surname}".trim()
                if (fullName.equals(FeaturedPlayer.name, ignoreCase = true)) {
                    juwan.add(cp)
                } else {
                    others.add(cp)
                }
            }
            // cardPlayers is a hash set; sort the rest so names, slugs and exports never depend on hash order.
            others.sortWith(
                compareBy<CardPlayer>({ it.player.surname.lowercase() }, { it.player.name.lowercase() }, { it.id.playerId })
            )
            return if (juwan.isEmpty()) others else (juwan + others)
        }

    /** Player, sport and team display names computed in a single sort pass (use when more than one is needed). */
    fun peopleSummary(): CardPeopleSummary {
        val sorted = sortedCardPlayers
        return CardPeopleSummary(
            playerNames = sorted.joinToString(", ") { "${it.player.name} ${it.player.surname}".trim() },
            sportNames = sorted.mapNotNull { it.player.sport?.name }.distinct().joinToString(", "),
            teamNames = sorted.mapNotNull { it.team?.name }.distinct().joinToString(", ")
        )
    }

    val playerNames: String
        get() = sortedCardPlayers.joinToString(", ") { "${it.player.name} ${it.player.surname}".trim() }

    val sportNames: String
        get() = sortedCardPlayers.mapNotNull { it.player.sport?.name }.distinct().joinToString(", ")

    val teamNames: String
        get() = sortedCardPlayers.mapNotNull { it.team?.name }.distinct().joinToString(", ")

    val verificationUrl: String?
        get() {
            val cert = gradingCertNumber?.trim()?.takeIf { it.isNotEmpty() } ?: return null
            return grading?.gradingCompany?.provider()?.verificationUrl(cert)
        }

    val frontScanUrl: String?
        get() {
            val cert = gradingCertNumber?.trim()?.takeIf { it.isNotEmpty() } ?: return null
            return grading?.gradingCompany?.provider()?.frontScanUrl(cert)
        }
}

data class CardPeopleSummary(val playerNames: String, val sportNames: String, val teamNames: String)
