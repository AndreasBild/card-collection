package de.maulmann.cardcollection.controller

import org.springframework.data.domain.Sort

data class SortableColumnInfo(val displayName: String, val propertyPath: String, val isSortable: Boolean = true)

/**
 * The `sort=property,direction` request parameter, parsed once.
 *
 * Two views are kept on purpose, exactly as the page always behaved: [sort] is what the query uses (an unknown
 * property falls back to `id` and sets [error]; `desc` and `descending` both mean descending), while
 * [currentProperty]/[currentDirection] are what the column headers use to draw their arrows (the raw property, and
 * descending only for `DESC`).
 */
class CardListSort private constructor(
    val sort: Sort,
    val error: String?,
    val currentProperty: String,
    val currentDirection: String
) {
    companion object {
        val COLUMNS = listOf(
            SortableColumnInfo("Player", "playerNames", false),
            SortableColumnInfo("Team", "teamNames", false),
            SortableColumnInfo("Sport", "sportNames", false),
            SortableColumnInfo("Season", "season.name"),
            SortableColumnInfo("Company", "manufacturer.name"),
            SortableColumnInfo("Brand", "brand.name"),
            SortableColumnInfo("Theme", "theme.name"),
            SortableColumnInfo("Variant", "variant.name"),
            SortableColumnInfo("Number", "number"),
            SortableColumnInfo("Serial", "serialNumber"),
            SortableColumnInfo("Print Run", "printRun"),
            SortableColumnInfo("Ratio", "packOdds"),
            SortableColumnInfo("Rookie", "rookieCard"),
            SortableColumnInfo("Game Used", "gameUsedMaterial"),
            SortableColumnInfo("Autograph", "autograph"),
            SortableColumnInfo("Grading Co.", "grading.gradingCompany"),
            SortableColumnInfo("Grade", "grading.grade")
        )

        private val DESCENDING_PATTERN = Regex("desc|descending", RegexOption.IGNORE_CASE)

        fun parse(raw: String?): CardListSort {
            if (raw == null) return CardListSort(Sort.by("id"), null, "id", "ASC")

            val parts = raw.split(",").map { it.trim() }
            val field = parts[0].takeIf { it.isNotBlank() } ?: "id"

            val queryDirection =
                if (parts.size > 1 && DESCENDING_PATTERN.matches(parts[1])) Sort.Direction.DESC else Sort.Direction.ASC
            val allowed = field == "id" || COLUMNS.any { it.isSortable && it.propertyPath == field }
            val sort = if (allowed) Sort.by(queryDirection, field) else Sort.by("id")
            val error = if (allowed) null else "Unsupported sort property: $field"

            val headerDirection =
                if (parts.size > 1 && parts[1].equals("DESC", ignoreCase = true)) "DESC" else "ASC"

            return CardListSort(sort, error, field, headerDirection)
        }
    }
}
