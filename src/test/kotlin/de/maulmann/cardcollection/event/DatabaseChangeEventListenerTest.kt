package de.maulmann.cardcollection.event

import de.maulmann.cardcollection.service.CardExportService
import de.maulmann.cardcollection.service.DatabaseDumpService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import java.io.File
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class DatabaseChangeEventListenerTest {

    private val cacheManager: CacheManager = mock()
    private val cardExportService: CardExportService = mock()
    private val databaseDumpService: DatabaseDumpService = mock()
    private val listener = DatabaseChangeEventListener(
        cacheManager,
        cardExportService,
        databaseDumpService,
        Executors.newSingleThreadExecutor()
    )

    private val event = DatabaseChangedEvent(previousSignature = "1:2:3", newSignature = "1:2:4")

    @AfterEach
    fun tearDown() {
        listener.shutdown()
    }

    @Test
    fun `onDatabaseChanged should clear all caches, trigger static site sync and trigger database dump`() {
        val mockCache1: Cache = mock()
        val mockCache2: Cache = mock()
        whenever(cacheManager.cacheNames).thenReturn(listOf("sports", "seasons"))
        whenever(cacheManager.getCache("sports")).thenReturn(mockCache1)
        whenever(cacheManager.getCache("seasons")).thenReturn(mockCache2)
        whenever(cardExportService.syncCardsJsonToStaticSite()).thenReturn(File("cards.json"))
        whenever(databaseDumpService.dumpAndBackup()).thenReturn(File("Dump.sql"))

        listener.onDatabaseChanged(event)

        // Eviction is synchronous, sync and dump run in the background.
        verify(mockCache1).clear()
        verify(mockCache2).clear()
        verify(cardExportService, timeout(2000)).syncCardsJsonToStaticSite()
        verify(databaseDumpService, timeout(2000)).dumpAndBackup()
    }

    @Test
    fun `onDatabaseChanged should handle sync failure gracefully and still dump`() {
        whenever(cacheManager.cacheNames).thenReturn(emptyList())
        whenever(cardExportService.syncCardsJsonToStaticSite()).thenThrow(RuntimeException("Disk write error"))
        whenever(databaseDumpService.dumpAndBackup()).thenReturn(File("Dump.sql"))

        listener.onDatabaseChanged(event)

        verify(cardExportService, timeout(2000)).syncCardsJsonToStaticSite()
        verify(databaseDumpService, timeout(2000)).dumpAndBackup()
    }

    @Test
    fun `onDatabaseChanged should handle dump failure gracefully without crashing`() {
        whenever(cacheManager.cacheNames).thenReturn(emptyList())
        whenever(cardExportService.syncCardsJsonToStaticSite()).thenReturn(File("cards.json"))
        whenever(databaseDumpService.dumpAndBackup()).thenThrow(RuntimeException("mysqldump failed"))

        listener.onDatabaseChanged(event)

        verify(cardExportService, timeout(2000)).syncCardsJsonToStaticSite()
        verify(databaseDumpService, timeout(2000)).dumpAndBackup()
    }

    @Test
    fun `slow dump does not block the publishing thread and caches are evicted before it returns`() {
        val cache: Cache = mock()
        whenever(cacheManager.cacheNames).thenReturn(listOf("sports"))
        whenever(cacheManager.getCache("sports")).thenReturn(cache)
        whenever(cardExportService.syncCardsJsonToStaticSite()).thenReturn(File("cards.json"))

        val dumpStarted = CountDownLatch(1)
        val releaseDump = CountDownLatch(1)
        whenever(databaseDumpService.dumpAndBackup()).thenAnswer {
            dumpStarted.countDown()
            releaseDump.await(5, TimeUnit.SECONDS)
            File("Dump.sql")
        }

        listener.onDatabaseChanged(event) // must return although the dump is about to block

        verify(cache).clear()
        assertTrue(dumpStarted.await(2, TimeUnit.SECONDS), "background dump should have started")
        releaseDump.countDown()
        verify(databaseDumpService, timeout(2000)).dumpAndBackup()
    }

    @Test
    fun `changes arriving during a run are coalesced into exactly one follow up run`() {
        whenever(cacheManager.cacheNames).thenReturn(emptyList())
        val syncCalls = AtomicInteger()
        val firstSyncStarted = CountDownLatch(1)
        val releaseFirstSync = CountDownLatch(1)
        whenever(cardExportService.syncCardsJsonToStaticSite()).thenAnswer {
            if (syncCalls.incrementAndGet() == 1) {
                firstSyncStarted.countDown()
                releaseFirstSync.await(5, TimeUnit.SECONDS)
            }
            File("cards.json")
        }
        whenever(databaseDumpService.dumpAndBackup()).thenReturn(null)

        listener.onDatabaseChanged(event)
        assertTrue(firstSyncStarted.await(2, TimeUnit.SECONDS))
        // Three more changes while the first run is still in flight.
        listener.onDatabaseChanged(event)
        listener.onDatabaseChanged(event)
        listener.onDatabaseChanged(event)
        releaseFirstSync.countDown()

        verify(cardExportService, timeout(3000).times(2)).syncCardsJsonToStaticSite()
        Thread.sleep(300)
        assertEquals(2, syncCalls.get(), "one initial run plus a single coalesced follow-up")
    }
}
