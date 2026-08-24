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
    var serialNumber: Int,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "season_id")
    var season: Season,
    var number: String,
    var rookieCard: Boolean,
    var gameUsedMaterial: Boolean,
    var autograph: Boolean,

    @OneToMany(mappedBy = "card", cascade = [CascadeType.ALL], orphanRemoval = true)
    @BatchSize(size = 20)
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
                if (fullName.equals("Juwan Howard", ignoreCase = true)) {
                    juwan.add(cp)
                } else {
                    others.add(cp)
                }
            }
            return if (juwan.isEmpty()) others else (juwan + others)
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
            return when (grading?.gradingCompany) {
                GradingCompany.PSA -> "https://www.psacard.com/cert/$cert"
                GradingCompany.BGS -> "https://www.beckett.com/grading/card-lookup?item_type=BGS&item_id=$cert"
                GradingCompany.MBA -> "https://www.mbagrading.com/cert/$cert"
                null -> null
            }
        }
}