package de.maulmann.cardcollection.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File

@Service
class DatabaseDumpService(
    @param:Value("\${export.dump.enabled:true}")
    private val dumpEnabled: Boolean = true,
    @param:Value("\${export.dump.script-path:scripts/sync_db_dump.py}")
    private val scriptPath: String = "scripts/sync_db_dump.py",
    @param:Value("\${export.dump.output-file:src/main/resources/sql/dump/Dump.sql}")
    private val dumpOutputFile: String = "src/main/resources/sql/dump/Dump.sql",
    @param:Value("\${export.dump.max-backups:30}")
    private val maxBackups: Int = 30
) {

    private val logger = LoggerFactory.getLogger(DatabaseDumpService::class.java)

    fun dumpAndBackup(): File? {
        if (!dumpEnabled) {
            logger.debug("Database dump sync is disabled by configuration (export.dump.enabled=false).")
            return null
        }

        val script = File(scriptPath)
        if (!script.exists()) {
            logger.warn("Database dump script not found at path: {}", script.absolutePath)
            return null
        }

        return try {
            val process = ProcessBuilder(
                "python3",
                script.absolutePath,
                "--max-backups",
                maxBackups.toString()
            )
                .redirectErrorStream(true)
                .start()

            val output = process.inputStream.bufferedReader().readText()
            val exitCode = process.waitFor()

            if (exitCode != 0) {
                logger.error("Database dump script failed with exit code {}: {}", exitCode, output.trim())
                null
            } else {
                logger.info("Database dump and rolling backup completed successfully:\n{}", output.trim())
                val targetDump = File(dumpOutputFile)
                if (targetDump.exists()) targetDump else null
            }
        } catch (e: Exception) {
            logger.error("Exception occurred while executing database dump script: {}", e.message, e)
            null
        }
    }
}
