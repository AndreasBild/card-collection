package de.maulmann.cardcollection.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.ALWAYS)
data class CardJsonDto(
    val id: String,
    val player: String?,
    val season: String?,
    val team: String?,
    val company: String?,
    val brand: String?,
    val theme: String?,
    val variant: String?,
    val cardNumber: String?,
    val serialNumber: String?,
    val printRun: Int?,
    val gradingCompany: String?,
    val grade: String?,
    @get:JsonProperty("isAutograph")
    val isAutograph: Boolean,
    @get:JsonProperty("isPatch")
    val isPatch: Boolean,
    @get:JsonProperty("isRookie")
    val isRookie: Boolean,
    val collection: String?,
    val notes: String? = null
)
