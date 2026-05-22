package de.maulmann.cardcollection.model

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*

@Entity
class CardBrand(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long = 0,
    var name: String,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "manufacturer_id") @JsonIgnore var manufacturer: CardManufacturer
)
