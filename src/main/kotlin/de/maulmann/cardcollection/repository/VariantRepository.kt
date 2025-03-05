package de.maulmann.cardcollection.repository

import org.springframework.data.jpa.repository.JpaRepository
import de.maulmann.cardcollection.model.Variant


interface VariantRepository : JpaRepository<Variant, Long>
