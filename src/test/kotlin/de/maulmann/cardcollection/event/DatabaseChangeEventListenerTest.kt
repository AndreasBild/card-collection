package de.maulmann.cardcollection.event

import de.maulmann.cardcollection.service.CardExportService
import de.maulmann.cardcollection.service.DatabaseDumpService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import java.io.File

class DatabaseChangeEventListenerTest {

    private val cacheManager: CacheManager = mock()
    private val cardExportService: CardExportService = mock()
    private val databaseDumpService: DatabaseDumpService = mock()
    private val listener = DatabaseChangeEventListener(cacheManager, cardExportService, databaseDumpService)

    @Test
    fun `onDatabaseChanged should clear all caches, trigger static site sync and trigger database dump`() {
        val mockCache1: Cache = mock()
        val mockCache2: Cache = mock()
        whenever(cacheManager.cacheNames).thenReturn(listOf("sports", "seasons"))
        whenever(cacheManager.getCache("sports")).thenReturn(mockCache1)
        whenever(cacheManager.getCache("seasons")).thenReturn(mockCache2)
        whenever(cardExportService.syncCardsJsonToStaticSite()).thenReturn(File("cards.json"))
        whenever(databaseDumpService.dumpAndBackup()).thenReturn(File("Dump.sql"))

        val event = DatabaseChangedEvent(
            previousSignature = "1:2:3",
            newSignature = "1:2:4"
        )

        listener.onDatabaseChanged(event)

        verify(mockCache1).clear()
        verify(mockCache2).clear()
        verify(cardExportService).syncCardsJsonToStaticSite()
        verify(databaseDumpService).dumpAndBackup()
    }

    @Test
    fun `onDatabaseChanged should handle sync failure gracefully without crashing`() {
        whenever(cacheManager.cacheNames).thenReturn(emptyList())
        whenever(cardExportService.syncCardsJsonToStaticSite()).thenThrow(RuntimeException("Disk write error"))
        whenever(databaseDumpService.dumpAndBackup()).thenReturn(File("Dump.sql"))

        val event = DatabaseChangedEvent(
            previousSignature = "1:2:3",
            newSignature = "1:2:4"
        )

        // Should not throw exception
        listener.onDatabaseChanged(event)

        verify(cardExportService).syncCardsJsonToStaticSite()
        verify(databaseDumpService).dumpAndBackup()
    }

    @Test
    fun `onDatabaseChanged should handle dump failure gracefully without crashing`() {
        whenever(cacheManager.cacheNames).thenReturn(emptyList())
        whenever(cardExportService.syncCardsJsonToStaticSite()).thenReturn(File("cards.json"))
        whenever(databaseDumpService.dumpAndBackup()).thenThrow(RuntimeException("mysqldump failed"))

        val event = DatabaseChangedEvent(
            previousSignature = "1:2:3",
            newSignature = "1:2:4"
        )

        // Should not throw exception
        listener.onDatabaseChanged(event)

        verify(cardExportService).syncCardsJsonToStaticSite()
        verify(databaseDumpService).dumpAndBackup()
    }
}
