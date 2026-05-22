package de.maulmann.cardcollection.model

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*

@Entity
class CardTheme(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long = 0,
    var name: String,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "brand_id") @JsonIgnore var brand: CardBrand?
)
