package de.maulmann.cardcollection.repository

import de.maulmann.cardcollection.model.Team
import org.springframework.data.jpa.repository.JpaRepository


interface TeamRepository : JpaRepository<Team, Long>
