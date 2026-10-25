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
) {
    /**
     * The active (non-null, non-blank) filters as query parameters in a stable order. Single source of truth for
     * rebuilding `/cards` links, so a new filter only has to be listed here.
     */
    fun toQueryParams(): Map<String, Any> = buildMap {
        manufacturerId?.let { put("manufacturerId", it) }
        brandId?.let { put("brandId", it) }
        themeId?.let { put("themeId", it) }
        sportId?.let { put("sportId", it) }
        playerId?.let { put("playerId", it) }
        teamId?.let { put("teamId", it) }
        seasonId?.let { put("seasonId", it) }
        variantId?.let { put("variantId", it) }
        gameUsed?.let { put("gameUsed", it) }
        autograph?.let { put("autograph", it) }
        rookieCard?.let { put("rookieCard", it) }
        printRunRangeKey?.takeIf { it.isNotBlank() }?.let { put("printRunRangeKey", it) }
        isGradedNullable?.let { put("isGradedNullable", it) }
    }
}
