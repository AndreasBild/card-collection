package de.maulmann.cardcollection.event

import de.maulmann.cardcollection.service.CardExportService
import de.maulmann.cardcollection.service.DatabaseDumpService
import org.slf4j.LoggerFactory
import org.springframework.cache.CacheManager
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class DatabaseChangeEventListener(
    private val cacheManager: CacheManager,
    private val cardExportService: CardExportService,
    private val databaseDumpService: DatabaseDumpService
) {

    private val logger = LoggerFactory.getLogger(DatabaseChangeEventListener::class.java)

    @EventListener
    fun onDatabaseChanged(event: DatabaseChangedEvent) {
        logger.info(
            "Handling DatabaseChangedEvent (signature: {} -> {}). Evicting caches, syncing static site and updating database dump...",
            event.previousSignature,
            event.newSignature
        )

        evictAllCaches()

        try {
            val syncedFile = cardExportService.syncCardsJsonToStaticSite()
            logger.info("Auto-sync completed successfully to {}", syncedFile.absolutePath)
        } catch (e: Exception) {
            logger.warn("Failed to auto-sync cards.json on DatabaseChangedEvent: {}", e.message)
        }

        try {
            val dumpFile = databaseDumpService.dumpAndBackup()
            if (dumpFile != null) {
                logger.info("Auto-dump and backup completed successfully to {}", dumpFile.absolutePath)
            }
        } catch (e: Exception) {
            logger.warn("Failed to auto-dump database on DatabaseChangedEvent: {}", e.message)
        }
    }

    fun evictAllCaches() {
        for (cacheName in cacheManager.cacheNames) {
            cacheManager.getCache(cacheName)?.clear()
        }
        logger.debug("Evicted all application Caffeine caches.")
    }
}
