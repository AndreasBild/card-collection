package de.maulmann.cardcollection.service

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class DatabaseDumpServiceTest {

    @Test
    fun `dumpAndBackup returns null when dump is disabled`() {
        val service = DatabaseDumpService(
            dumpEnabled = false,
            scriptPath = "scripts/sync_db_dump.py"
        )

        assertNull(service.dumpAndBackup())
    }

    @Test
    fun `dumpAndBackup returns null when script file does not exist`() {
        val service = DatabaseDumpService(
            dumpEnabled = true,
            scriptPath = "non_existent_script.py"
        )

        assertNull(service.dumpAndBackup())
    }

    @Test
    fun `dumpAndBackup executes mock script and returns output file`(@TempDir tempDir: Path) {
        val mockScript = tempDir.resolve("mock_dump.py").toFile()
        val mockDump = tempDir.resolve("Dump.sql").toFile()

        mockScript.writeText(
            """
            import sys
            from pathlib import Path
            Path("${mockDump.absolutePath}").write_text("-- mock dump\n")
            print("Successfully wrote mock dump")
            """.trimIndent()
        )

        val service = DatabaseDumpService(
            dumpEnabled = true,
            scriptPath = mockScript.absolutePath,
            dumpOutputFile = mockDump.absolutePath,
            maxBackups = 5
        )

        val result = service.dumpAndBackup()
        assertNotNull(result)
        assertEquals(mockDump.absolutePath, result.absolutePath)
        assertEquals("-- mock dump\n", mockDump.readText())
    }
}
