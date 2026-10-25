package de.maulmann.cardcollection.controller

import de.maulmann.cardcollection.dto.CardFilter
import de.maulmann.cardcollection.model.CardBrand
import de.maulmann.cardcollection.model.CardManufacturer
import de.maulmann.cardcollection.model.CardTheme
import de.maulmann.cardcollection.model.Player
import de.maulmann.cardcollection.model.Season
import de.maulmann.cardcollection.model.Sport
import de.maulmann.cardcollection.model.Team
import de.maulmann.cardcollection.model.Variant
import de.maulmann.cardcollection.service.PrintRunRange
import org.springframework.web.util.UriComponentsBuilder

data class ActiveFilterChip(val label: String, val value: String, val removeUrl: String)

/** The reference lists used to turn filter ids into display names. */
data class FilterLookups(
    val manufacturers: List<CardManufacturer>,
    val brands: List<CardBrand>,
    val themes: List<CardTheme>,
    val sports: List<Sport>,
    val players: List<Player>,
    val teams: List<Team>,
    val seasons: List<Season>,
    val variants: List<Variant>
)

/** One removable chip per active filter; each chip links to the same list without that filter. */
object ActiveFilterChips {

    private const val DEFAULT_SIZE = "20"

    fun build(filter: CardFilter, lookups: FilterLookups, size: String, sort: String?): List<ActiveFilterChip> {
        val chips = mutableListOf<ActiveFilterChip>()

        fun urlWithout(paramKey: String): String {
            val builder = UriComponentsBuilder.fromPath("/cards")
            if (size != DEFAULT_SIZE) builder.queryParam("size", size)
            if (!sort.isNullOrBlank()) builder.queryParam("sort", sort)

            filter.toQueryParams().forEach { (name, value) -> if (name != paramKey) builder.queryParam(name, value) }

            return builder.build().toUriString()
        }

        filter.manufacturerId?.let { id ->
            val name = lookups.manufacturers.find { it.id == id }?.name ?: id.toString()
            chips.add(ActiveFilterChip("Manufacturer", name, urlWithout("manufacturerId")))
        }
        filter.brandId?.let { id ->
            val name = lookups.brands.find { it.id == id }?.name ?: id.toString()
            chips.add(ActiveFilterChip("Brand", name, urlWithout("brandId")))
        }
        filter.themeId?.let { id ->
            val name = lookups.themes.find { it.id == id }?.name ?: id.toString()
            chips.add(ActiveFilterChip("Theme", name, urlWithout("themeId")))
        }
        filter.sportId?.let { id ->
            val name = lookups.sports.find { it.id == id }?.name ?: id.toString()
            chips.add(ActiveFilterChip("Sport", name, urlWithout("sportId")))
        }
        filter.playerId?.let { id ->
            val name = lookups.players.find { it.id == id }?.let { "${it.name} ${it.surname}".trim() } ?: id.toString()
            chips.add(ActiveFilterChip("Player", name, urlWithout("playerId")))
        }
        filter.teamId?.let { id ->
            val name = lookups.teams.find { it.id == id }?.name ?: id.toString()
            chips.add(ActiveFilterChip("Team", name, urlWithout("teamId")))
        }
        filter.seasonId?.let { id ->
            val name = lookups.seasons.find { it.id == id }?.name ?: id.toString()
            chips.add(ActiveFilterChip("Season", name, urlWithout("seasonId")))
        }
        filter.variantId?.let { id ->
            val name = lookups.variants.find { it.id == id }?.name ?: id.toString()
            chips.add(ActiveFilterChip("Variant", name, urlWithout("variantId")))
        }
        filter.gameUsed?.let {
            chips.add(ActiveFilterChip("Game Used", if (it) "Yes" else "No", urlWithout("gameUsed")))
        }
        filter.autograph?.let {
            chips.add(ActiveFilterChip("Autograph", if (it) "Yes" else "No", urlWithout("autograph")))
        }
        filter.rookieCard?.let {
            chips.add(ActiveFilterChip("Rookie", if (it) "Yes" else "No", urlWithout("rookieCard")))
        }
        filter.printRunRangeKey?.takeIf { it.isNotBlank() }?.let { key ->
            val displayName = PrintRunRange.fromKey(key)?.displayName ?: key
            chips.add(ActiveFilterChip("Print Run", displayName, urlWithout("printRunRangeKey")))
        }
        filter.isGradedNullable?.let {
            chips.add(ActiveFilterChip("Graded", if (it) "Yes" else "No", urlWithout("isGradedNullable")))
        }

        return chips
    }
}
