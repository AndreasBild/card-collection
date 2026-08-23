package de.maulmann.cardcollection.dto

import java.time.Instant

data class SyncStatusResponse(
    val status: String,
    val syncedFile: String,
    val exists: Boolean,
    val fileSizeBytes: Long,
    val cardCount: Int,
    val timestamp: Instant = Instant.now()
)
