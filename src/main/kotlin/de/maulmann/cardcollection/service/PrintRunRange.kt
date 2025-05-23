package de.maulmann.cardcollection.service

enum class PrintRunRange(
    val key: String,
    val displayName: String,
    val maxValue: Int? = null,
    val exactValue: Int? = null,
    val isAllWithPrintRun: Boolean = false
) {
    ONE("ONE", "1", exactValue = 1),
    LE_10("LE_10", "<= 10", maxValue = 10),
    LE_50("LE_50", "<= 50", maxValue = 50),
    LE_100("LE_100", "<= 100", maxValue = 100),
    LE_500("LE_500", "<= 500", maxValue = 500),
    LE_1000("LE_1000", "<= 1000", maxValue = 1000),
    ALL_WITH_PRINT_RUN("ALL_WITH_PRINT_RUN", "Any Card with Print Run", isAllWithPrintRun = true);

    companion object {
        fun fromKey(key: String?): PrintRunRange? {
            // Using .values() is efficient for enums
            return values().find { it.key == key }
        }
    }
}
