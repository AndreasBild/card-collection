package de.maulmann.cardcollection.model

import jakarta.persistence.*

@Entity
class CardTheme(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long = 0,
    var name: String
)
