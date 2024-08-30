package de.maulmann.cardcollection.repository


import de.maulmann.cardcollection.model.CardBrand
import org.springframework.data.jpa.repository.JpaRepository

interface CardBrandRepository : JpaRepository<CardBrand, Long>
