package de.maulmann.cardcollection.model


import jakarta.persistence.*

@Entity
data class CardBrand(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val name: String,

    @ManyToOne
    @JoinColumn(name = "manufacturer_id")
    val manufacturer: CardManufacturer
)
