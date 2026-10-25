package de.maulmann.cardcollection.event

import de.maulmann.cardcollection.service.CardExportService
import de.maulmann.cardcollection.service.DatabaseDumpService
import jakarta.annotation.PreDestroy
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.CacheManager
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Reacts to [DatabaseChangedEvent]s published by the change detector.
 *
 * Cache eviction is fast and must happen before anything reads again, so it runs on the publishing thread. The
 * slow work (cards.json sync, mysqldump) runs on one background thread so it can never delay the next
 * 30-second poll. Changes that arrive while a run is in progress are coalesced into exactly one follow-up run,
 * which re-reads the database and therefore already contains every earlier change.
 */
@Component
class DatabaseChangeEventListener internal constructor(
    private val cacheManager: CacheManager,
    private val cardExportService: CardExportService,
    private val databaseDumpService: DatabaseDumpService,
    private val executor: ExecutorService
) {

    @Autowired
    constructor(
        cacheManager: CacheManager,
        cardExportService: CardExportService,
        databaseDumpService: DatabaseDumpService
    ) : this(
        cacheManager,
        cardExportService,
        databaseDumpService,
        Executors.newSingleThreadExecutor(Thread.ofPlatform().name("db-change-sync").daemon(true).factory())
    )

    private val logger = LoggerFactory.getLogger(DatabaseChangeEventListener::class.java)
    private val running = AtomicBoolean(false)
    private val rerunRequested = AtomicBoolean(false)

    @EventListener
    fun onDatabaseChanged(event: DatabaseChangedEvent) {
        logger.info(
            "Handling DatabaseChangedEvent (signature: {} -> {}). Evicting caches; syncing static site and updating database dump in the background...",
            event.previousSignature,
            event.newSignature
        )

        evictAllCaches()
        scheduleSyncAndDump()
    }

    private fun scheduleSyncAndDump() {
        if (!running.compareAndSet(false, true)) {
            // A run is in flight: it may already have read the database, so ask for one more pass.
            rerunRequested.set(true)
            return
        }
        try {
            executor.execute {
                try {
                    do {
                        rerunRequested.set(false)
                        syncAndDump()
                    } while (rerunRequested.get())
                } finally {
                    running.set(false)
                }
                // A request that slipped in between the loop check and the flag reset must not be lost.
                if (rerunRequested.get()) scheduleSyncAndDump()
            }
        } catch (e: Exception) {
            running.set(false)
            logger.warn("Could not schedule background sync/dump after database change: {}", e.message)
        }
    }

    private fun syncAndDump() {
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

    @PreDestroy
    fun shutdown() {
        executor.shutdown()
        if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
            executor.shutdownNow()
        }
    }
}
