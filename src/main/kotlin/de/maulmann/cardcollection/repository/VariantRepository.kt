package de.maulmann.cardcollection.repository

import de.maulmann.cardcollection.model.Variant
import org.springframework.data.jpa.repository.JpaRepository


interface VariantRepository : JpaRepository<Variant, Long>
