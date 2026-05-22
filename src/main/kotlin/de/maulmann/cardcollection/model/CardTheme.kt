package de.maulmann.cardcollection.model

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*

@Entity
class CardTheme(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long = 0,
    val name: String,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "brand_id") @JsonIgnore val brand: CardBrand?
)
