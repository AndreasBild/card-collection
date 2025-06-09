package de.maulmann.cardcollection.model

import jakarta.persistence.*
import jakarta.persistence.Table

@Entity
@Table(name="Sport")
data class Sport(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val name: String
)
