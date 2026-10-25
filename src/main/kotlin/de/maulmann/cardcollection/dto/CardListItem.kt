package de.maulmann.cardcollection.dto

import de.maulmann.cardcollection.model.Card
import de.maulmann.cardcollection.model.GradingCompany

/**
 * Immutable read model of one row in the card list. It carries exactly what the page and its JSON-LD need, fully
 * resolved, so cached pages hold no JPA entities (no lazy state, no shared mutable objects, no detached proxies).
 * Property names mirror the entity so the template expressions stay unchanged.
 */
data class CardListItem(
    val id: Long,
    val playerNames: String,
    val teamNames: String,
    val sportNames: String,
    val season: NamedRef,
    val manufacturer: NamedRef,
    val brand: NamedRef,
    val theme: NamedRef,
    val variant: NamedRef,
    val number: String,
    val serialNumber: Int,
    val printRun: Int?,
    val packOdds: String?,
    val rookieCard: Boolean,
    val gameUsedMaterial: Boolean,
    val autograph: Boolean,
    val grading: GradingView?,
    val gradingCertNumber: String?,
    val verificationUrl: String?,
    val frontScanUrl: String?
) {
    data class NamedRef(val name: String)

    data class GradingView(val grade: Float?, val gradingCompany: GradingCompany?)

    companion object {
        /** Must be called while the card's associations are initialised (inside the service transaction). */
        fun from(card: Card): CardListItem {
            val people = card.peopleSummary()
            return CardListItem(
                id = card.id,
                playerNames = people.playerNames,
                teamNames = people.teamNames,
                sportNames = people.sportNames,
                season = NamedRef(card.season.name),
                manufacturer = NamedRef(card.manufacturer.name),
                brand = NamedRef(card.brand.name),
                theme = NamedRef(card.theme.name),
                variant = NamedRef(card.variant.name),
                number = card.number,
                serialNumber = card.serialNumber,
                printRun = card.printRun,
                packOdds = card.packOdds,
                rookieCard = card.rookieCard,
                gameUsedMaterial = card.gameUsedMaterial,
                autograph = card.autograph,
                grading = card.grading?.let { GradingView(it.grade, it.gradingCompany) },
                gradingCertNumber = card.gradingCertNumber,
                verificationUrl = card.verificationUrl,
                frontScanUrl = card.frontScanUrl
            )
        }
    }
}
