package de.maulmann.cardcollection.actuator

import de.maulmann.cardcollection.repository.CardRepository
import de.maulmann.cardcollection.repository.PlayerRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.actuate.info.Info
import org.springframework.boot.actuate.info.InfoContributor
import org.springframework.stereotype.Component

@Component
class CardCollectionInfoContributor(
    private val cardRepository: CardRepository,
    private val playerRepository: PlayerRepository,
    @Value("\${export.json.sync-path:../card-collectionJava/content/json/cards.json}")
    private val syncPath: String
) : InfoContributor {

    override fun contribute(builder: Info.Builder) {
        val totalCards = cardRepository.count()
        val totalPlayers = playerRepository.count()

        val collectionStats = mapOf(
            "totalCards" to totalCards,
            "totalPlayers" to totalPlayers,
            "syncPath" to syncPath
        )

        builder.withDetail("cardCollection", collectionStats)
    }
}
