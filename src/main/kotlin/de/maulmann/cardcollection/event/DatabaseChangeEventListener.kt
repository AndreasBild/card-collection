package de.maulmann.cardcollection.event

import de.maulmann.cardcollection.service.CardExportService
import org.slf4j.LoggerFactory
import org.springframework.cache.CacheManager
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class DatabaseChangeEventListener(
    private val cacheManager: CacheManager,
    private val cardExportService: CardExportService
) {

    private val logger = LoggerFactory.getLogger(DatabaseChangeEventListener::class.java)

    @EventListener
    fun onDatabaseChanged(event: DatabaseChangedEvent) {
        logger.info(
            "Handling DatabaseChangedEvent (signature: {} -> {}). Evicting caches and syncing static site...",
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
    }

    fun evictAllCaches() {
        for (cacheName in cacheManager.cacheNames) {
            cacheManager.getCache(cacheName)?.clear()
        }
        logger.debug("Evicted all application Caffeine caches.")
    }
}
