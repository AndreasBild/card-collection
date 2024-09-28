package de.maulmann.cardcollection.repository


import de.maulmann.cardcollection.model.Sport
import org.springframework.data.jpa.repository.JpaRepository

interface SportRepository : JpaRepository<Sport, Long>
