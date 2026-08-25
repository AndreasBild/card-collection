package de.maulmann.cardcollection.dto

data class GradingScanCardResult(
    val cardId: Long,
    val company: String,
    val certNumber: String,
    val frontImageDownloaded: Boolean,
    val backImageDownloaded: Boolean,
    val frontImagePath: String? = null,
    val backImagePath: String? = null,
    val status: String,
    val message: String? = null
)

data class GradingScanDownloadSummary(
    val totalGradedCards: Int,
    val successfulDownloads: Int,
    val alreadyPresent: Int,
    val notAvailableOrFailed: Int,
    val outputDirectory: String,
    val cardResults: List<GradingScanCardResult>
)
