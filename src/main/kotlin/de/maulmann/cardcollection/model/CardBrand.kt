package de.maulmann.cardcollection.model

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*

@Entity
class CardBrand(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long = 0,
    val name: String,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "manufacturer_id") @JsonIgnore val manufacturer: CardManufacturer
)
