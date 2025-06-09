package de.maulmann.cardcollection.model

import jakarta.persistence.*
import jakarta.persistence.Table

@Entity

data class Team(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val name: String
)
