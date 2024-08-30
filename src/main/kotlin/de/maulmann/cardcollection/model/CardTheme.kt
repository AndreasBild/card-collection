package de.maulmann.cardcollection.model

import jakarta.persistence.*

@Entity
data class CardTheme(


    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val name: String,

    @ManyToOne
    @JoinColumn(name = "brand_id")
    val brand: CardBrand
)
