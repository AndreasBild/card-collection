package de.maulmann.cardcollection.service

import de.maulmann.cardcollection.event.DatabaseChangedEvent
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.cache.CacheManager
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
    private val cacheManager: CacheManager,
    private val cardExportService: CardExportService,
    private val eventPublisher: ApplicationEventPublisher,
    @Value("\${export.db-sync.sync-on-startup:false}")
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

    @Scheduled(fixedDelayString = "\${export.db-sync.polling-interval-ms:3000}")
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

    fun evictAllCaches() {
        for (cacheName in cacheManager.cacheNames) {
            cacheManager.getCache(cacheName)?.clear()
        }
        logger.debug("Evicted all application Caffeine caches.")
    }

    fun computeDatabaseSignature(): String {
        val sql = """
            SELECT 
                (SELECT COUNT(*) FROM card),
                (SELECT COALESCE(SUM(id + serial_number + COALESCE(print_run, 0) + COALESCE(grading_id, 0) + COALESCE(season_id, 0) + COALESCE(manufacturer_id, 0) + COALESCE(brand_id, 0) + COALESCE(theme_id, 0) + COALESCE(variant_id, 0)), 0) FROM card),
                (SELECT COUNT(*) FROM card_player),
                (SELECT COALESCE(SUM(card_id + player_id + COALESCE(team_id, 0)), 0) FROM card_player),
                (SELECT COUNT(*) FROM player),
                (SELECT COALESCE(SUM(id + sport_id), 0) FROM player),
                (SELECT COUNT(*) FROM grading),
                (SELECT COALESCE(SUM(id + CAST(grade * 10 AS INT)), 0) FROM grading),
                (SELECT COUNT(*) FROM team),
                (SELECT COUNT(*) FROM season),
                (SELECT COUNT(*) FROM card_brand),
                (SELECT COUNT(*) FROM card_theme),
                (SELECT COUNT(*) FROM variant),
                (SELECT COUNT(*) FROM card_manufacturer)
        """.trimIndent()

        return jdbcTemplate.queryForObject(sql) { rs, _ ->
            (1..14).joinToString(":") { rs.getLong(it).toString() }
        } ?: "0"
    }

    fun getCurrentSignature(): String? = lastStateSignature.get()

    fun resetSignature(newSignature: String?) {
        lastStateSignature.set(newSignature)
    }

    fun getLastSyncTimestamp(): Instant? = lastSyncTimestamp.get()

    fun setLastSyncTimestamp(timestamp: Instant?) {
        lastSyncTimestamp.set(timestamp)
    }
}
