package de.maulmann.cardcollection.model

import jakarta.persistence.*
import java.io.Serializable

@Embeddable
data class CardPlayerId(
    @Column(name = "card_id") val cardId: Long = 0,
    @Column(name = "player_id") val playerId: Long = 0
) : Serializable

@Entity
@Table(name = "card_player")
class CardPlayer(
    @EmbeddedId
    val id: CardPlayerId,

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("cardId")
    @JoinColumn(name = "card_id")
    val card: Card,

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("playerId")
    @JoinColumn(name = "player_id")
    val player: Player,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    val team: Team? = null
)