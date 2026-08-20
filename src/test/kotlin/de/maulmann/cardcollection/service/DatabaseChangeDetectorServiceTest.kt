package de.maulmann.cardcollection.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.any
import org.mockito.Mockito.eq
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import java.io.File

@ExtendWith(MockitoExtension::class)
class DatabaseChangeDetectorServiceTest {

    @Mock
    private lateinit var jdbcTemplate: JdbcTemplate

    @Mock
    private lateinit var cacheManager: CacheManager

    @Mock
    private lateinit var cache: Cache

    @Mock
    private lateinit var cardExportService: CardExportService

    @Test
    fun `test init sets initial signature without triggering sync when syncOnStartup is false`() {
        val dummySig = "10:100:10:200:1:2:0:0:1:1:1:1:1:1"
        `when`(jdbcTemplate.queryForObject(any(String::class.java), any<RowMapper<String>>()))
            .thenReturn(dummySig)

        val service = DatabaseChangeDetectorService(
            jdbcTemplate = jdbcTemplate,
            cacheManager = cacheManager,
            cardExportService = cardExportService,
            syncOnStartup = false
        )

        service.init()

        assertEquals(dummySig, service.getCurrentSignature())
        verify(cardExportService, never()).syncCardsJsonToStaticSite()
    }

    @Test
    fun `test checkForDatabaseChanges triggers cache eviction and sync on signature change`() {
        val initialSig = "10:100:10:200:1:2:0:0:1:1:1:1:1:1"
        val modifiedSig = "11:150:11:220:1:2:0:0:1:1:1:1:1:1"

        `when`(jdbcTemplate.queryForObject(any(String::class.java), any<RowMapper<String>>()))
            .thenReturn(initialSig)
            .thenReturn(modifiedSig)

        `when`(cacheManager.cacheNames).thenReturn(listOf("filteredCards", "players"))
        `when`(cacheManager.getCache("filteredCards")).thenReturn(cache)
        `when`(cacheManager.getCache("players")).thenReturn(cache)
        `when`(cardExportService.syncCardsJsonToStaticSite()).thenReturn(File("cards.json"))

        val service = DatabaseChangeDetectorService(
            jdbcTemplate = jdbcTemplate,
            cacheManager = cacheManager,
            cardExportService = cardExportService,
            syncOnStartup = false
        )

        service.init()
        assertEquals(initialSig, service.getCurrentSignature())

        // Check for changes -> signature changed!
        val changed = service.checkForDatabaseChanges()

        assertTrue(changed)
        assertEquals(modifiedSig, service.getCurrentSignature())
        verify(cacheManager).getCache("filteredCards")
        verify(cacheManager).getCache("players")
        verify(cache, org.mockito.Mockito.times(2)).clear()
        verify(cardExportService).syncCardsJsonToStaticSite()
    }

    @Test
    fun `test checkForDatabaseChanges returns false when signature unchanged`() {
        val sameSig = "10:100:10:200:1:2:0:0:1:1:1:1:1:1"

        `when`(jdbcTemplate.queryForObject(any(String::class.java), any<RowMapper<String>>()))
            .thenReturn(sameSig)

        val service = DatabaseChangeDetectorService(
            jdbcTemplate = jdbcTemplate,
            cacheManager = cacheManager,
            cardExportService = cardExportService,
            syncOnStartup = false
        )

        service.init()

        val changed = service.checkForDatabaseChanges()

        assertFalse(changed)
        verify(cardExportService, never()).syncCardsJsonToStaticSite()
    }
}
