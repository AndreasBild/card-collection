package de.maulmann.cardcollection.service

import de.maulmann.cardcollection.event.DatabaseChangedEvent
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.ApplicationEventPublisher
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.concurrent.atomic.AtomicReference

@Service
@ConditionalOnProperty(name = ["export.db-sync.enabled"], havingValue = "true", matchIfMissing = true)
class DatabaseChangeDetectorService(
    private val jdbcTemplate: JdbcTemplate,
    private val cardExportService: CardExportService,
    private val eventPublisher: ApplicationEventPublisher,
    @param:Value("\${export.db-sync.sync-on-startup:false}")
    private val syncOnStartup: Boolean = false
) {

    private val logger = LoggerFactory.getLogger(DatabaseChangeDetectorService::class.java)
    private val lastStateSignature = AtomicReference<String?>(null)
    private val lastSyncTimestamp = AtomicReference<Instant?>(null)

    @PostConstruct
    fun init() {
        try {
            val initialSignature = computeDatabaseSignature()
            lastStateSignature.set(initialSignature)
            logger.info("Initialized DatabaseChangeDetector with signature: {}", initialSignature)

            if (syncOnStartup) {
                val syncedFile = cardExportService.syncCardsJsonToStaticSite()
                lastSyncTimestamp.set(Instant.now())
                logger.info("Initial sync on startup completed to {}", syncedFile.absolutePath)
            }
        } catch (e: Exception) {
            logger.warn("Initialization of DatabaseChangeDetector signature deferred: {}", e.message)
        }
    }

    @Scheduled(fixedDelayString = "\${export.db-sync.polling-interval-ms:30000}")
    fun checkForDatabaseChanges(): Boolean {
        return try {
            val currentSignature = computeDatabaseSignature()
            val previousSignature = lastStateSignature.get()

            if (previousSignature != null && currentSignature != previousSignature) {
                logger.info(
                    "Detected external database change (signature: {} -> {}). Publishing DatabaseChangedEvent...",
                    previousSignature,
                    currentSignature
                )
                lastStateSignature.set(currentSignature)
                lastSyncTimestamp.set(Instant.now())
                eventPublisher.publishEvent(DatabaseChangedEvent(previousSignature, currentSignature))
                true
            } else {
                if (previousSignature == null) {
                    lastStateSignature.set(currentSignature)
                }
                false
            }
        } catch (e: Exception) {
            logger.warn("Database change detection check failed: {}", e.message)
            false
        }
    }

    fun computeDatabaseSignature(): String {
        return jdbcTemplate.queryForObject(SIGNATURE_SQL) { rs, _ ->
            (1..SIGNATURE_COLUMN_COUNT).joinToString(":") { rs.getLong(it).toString() }
        }
    }

    companion object {
        /**
         * Every exported table with the expressions that make up one row's content. Each expression is wrapped
         * in IFNULL so a NULL cannot collide with an empty value; bit columns are cast with `+ 0`.
         */
        internal val SIGNATURE_TABLES: Map<String, List<String>> = linkedMapOf(
            "card" to listOf(
                "id", "print_run", "pack_odds", "serial_number", "number", "theme_id", "season_id", "variant_id",
                "rookie_card + 0", "game_used_material + 0", "autograph + 0", "grading_id", "grading_cert_number",
                "manufacturer_id", "brand_id"
            ),
            "card_player" to listOf("card_id", "player_id", "team_id"),
            "player" to listOf("id", "name", "surname", "sport_id"),
            "grading" to listOf("id", "grade", "grading_company"),
            "sport" to listOf("id", "name"),
            "team" to listOf("id", "name"),
            "season" to listOf("id", "name"),
            "card_brand" to listOf("id", "name"),
            "card_theme" to listOf("id", "name"),
            "variant" to listOf("id", "name"),
            "card_manufacturer" to listOf("id", "name")
        )

        /** Two values per table: row count and the sum of per-row CRC32 content hashes. */
        internal val SIGNATURE_COLUMN_COUNT = SIGNATURE_TABLES.size * 2

        /**
         * One round trip, one scan per table. Counts catch inserts/deletes; the CRC32 sum catches any edit of an
         * exported column (renames, re-assignments, grading changes), which id/length sums could miss.
         */
        internal val SIGNATURE_SQL: String = SIGNATURE_TABLES.entries.joinToString(
            separator = "\nCROSS JOIN\n",
            prefix = "SELECT " + SIGNATURE_TABLES.keys.joinToString(", ") { "`$it`.cnt, `$it`.crc" } + "\nFROM\n"
        ) { (table, columns) ->
            val rowContent = columns.joinToString(", ") { "IFNULL($it, '~')" }
            "(SELECT COUNT(*) AS cnt, COALESCE(SUM(CRC32(CONCAT_WS('|', $rowContent))), 0) AS crc FROM `$table`) `$table`"
        }
    }

    fun getCurrentSignature(): String? = lastStateSignature.get()

    fun getLastSyncTimestamp(): Instant? = lastSyncTimestamp.get()
}
