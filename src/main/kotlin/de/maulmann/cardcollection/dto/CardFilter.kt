package de.maulmann.cardcollection.dto

data class CardFilter(
    val manufacturerId: Long? = null,
    val brandId: Long? = null,
    val themeId: Long? = null,
    val sportId: Long? = null,
    val playerId: Long? = null,
    val seasonId: Long? = null,
    val gameUsed: Boolean? = null,
    val autograph: Boolean? = null,
    val variantId: Long? = null,
    val rookieCard: Boolean? = null,
    val printRunRangeKey: String? = null,
    val teamId: Long? = null,
    val isGradedNullable: Boolean? = null
)
