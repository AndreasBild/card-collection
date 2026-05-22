package de.maulmann.cardcollection.model


import jakarta.persistence.*
import com.fasterxml.jackson.annotation.JsonIgnore

@Entity
class CardBrand(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long = 0, val name: String,

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "manufacturer_id") @JsonIgnore

    val manufacturer: CardManufacturer
)
