package de.maulmann.cardcollection.event

import de.maulmann.cardcollection.service.CardExportService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import java.io.File
import kotlin.test.assertEquals

class DatabaseChangeEventListenerTest {

    private val cacheManager: CacheManager = mock()
    private val cardExportService: CardExportService = mock()
    private val listener = DatabaseChangeEventListener(cacheManager, cardExportService)

    @Test
    fun `onDatabaseChanged should clear all caches and trigger static site sync`() {
        val mockCache1: Cache = mock()
        val mockCache2: Cache = mock()
        whenever(cacheManager.cacheNames).thenReturn(listOf("sports", "seasons"))
        whenever(cacheManager.getCache("sports")).thenReturn(mockCache1)
        whenever(cacheManager.getCache("seasons")).thenReturn(mockCache2)
        whenever(cardExportService.syncCardsJsonToStaticSite()).thenReturn(File("cards.json"))

        val event = DatabaseChangedEvent(
            previousSignature = "1:2:3",
            newSignature = "1:2:4"
        )

        listener.onDatabaseChanged(event)

        verify(mockCache1).clear()
        verify(mockCache2).clear()
        verify(cardExportService).syncCardsJsonToStaticSite()
    }

    @Test
    fun `onDatabaseChanged should handle sync failure gracefully without crashing`() {
        whenever(cacheManager.cacheNames).thenReturn(emptyList())
        whenever(cardExportService.syncCardsJsonToStaticSite()).thenThrow(RuntimeException("Disk write error"))

        val event = DatabaseChangedEvent(
            previousSignature = "1:2:3",
            newSignature = "1:2:4"
        )

        // Should not throw exception
        listener.onDatabaseChanged(event)

        verify(cardExportService).syncCardsJsonToStaticSite()
    }
}
