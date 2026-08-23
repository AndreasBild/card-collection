package de.maulmann.cardcollection.event

import java.time.Instant

data class DatabaseChangedEvent(
    val previousSignature: String?,
    val newSignature: String,
    val timestamp: Instant = Instant.now()
)
