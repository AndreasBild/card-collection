package de.maulmann.cardcollection.model

import jakarta.persistence.*
import org.hibernate.annotations.BatchSize
import java.io.Serializable

@Embeddable
data class CardPlayerId(
    @Column(name = "card_id") var cardId: Long = 0,
    @Column(name = "player_id") var playerId: Long = 0
) : Serializable

@Entity
@Table(name = "card_player")
@BatchSize(size = 50)
class CardPlayer(
    @EmbeddedId
    var id: CardPlayerId,

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("cardId")
    @JoinColumn(name = "card_id")
    var card: Card,

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("playerId")
    @JoinColumn(name = "player_id")
    var player: Player,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    var team: Team? = null
)