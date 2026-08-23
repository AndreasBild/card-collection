package de.maulmann.cardcollection.actuator

import de.maulmann.cardcollection.service.DatabaseChangeDetectorService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.boot.health.contributor.Status
import java.io.File
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class DatabaseSyncHealthIndicatorTest {

    @Test
    fun `health should return UP when sync directory is valid`(@TempDir tempDir: File) {
        val targetFile = File(tempDir, "cards.json")
        targetFile.writeText("[]")

        val changeDetectorService: DatabaseChangeDetectorService = mock()
        whenever(changeDetectorService.getCurrentSignature()).thenReturn("10:20:30")
        whenever(changeDetectorService.getLastSyncTimestamp()).thenReturn(Instant.parse("2026-08-23T10:00:00Z"))

        val indicator = DatabaseSyncHealthIndicator(targetFile.absolutePath, changeDetectorService)
        val health = indicator.health()

        assertEquals(Status.UP, health.status)
        assertEquals(targetFile.absolutePath, health.details["syncPath"])
        assertEquals(true, health.details["fileExists"])
        assertEquals(2L, health.details["fileSizeBytes"])
        assertEquals("10:20:30", health.details["lastSignature"])
        assertEquals("2026-08-23T10:00:00Z", health.details["lastSyncTimestamp"])
    }

    @Test
    fun `health should return UP with fallback values when service is null`(@TempDir tempDir: File) {
        val targetFile = File(tempDir, "cards.json")
        val indicator = DatabaseSyncHealthIndicator(targetFile.absolutePath, null)
        val health = indicator.health()

        assertEquals(Status.UP, health.status)
        assertEquals(false, health.details["fileExists"])
        assertEquals(0L, health.details["fileSizeBytes"])
        assertEquals("N/A", health.details["lastSignature"])
        assertEquals("N/A", health.details["lastSyncTimestamp"])
    }
}
