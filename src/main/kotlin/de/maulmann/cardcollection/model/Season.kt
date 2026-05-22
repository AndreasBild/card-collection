package de.maulmann.cardcollection.model

import jakarta.persistence.*

@Entity
class Season(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(nullable = false, unique = true) // Seasons should be unique and not null
    var name: String
)
