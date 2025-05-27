package de.maulmann.cardcollection.model

import jakarta.persistence.*

@Entity
data class Variant(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val name: String,

    @ManyToOne
    @JoinColumn(name = "theme_id")
    val theme: CardTheme? = null
)
