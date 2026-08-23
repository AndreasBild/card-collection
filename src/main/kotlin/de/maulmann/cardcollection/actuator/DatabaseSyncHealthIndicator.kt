package de.maulmann.cardcollection.actuator

import de.maulmann.cardcollection.service.DatabaseChangeDetectorService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.health.contributor.Health
import org.springframework.boot.health.contributor.HealthIndicator
import org.springframework.stereotype.Component
import java.io.File

@Component
class DatabaseSyncHealthIndicator(
    @Value("\${export.json.sync-path:../card-collectionJava/content/json/cards.json}")
    private val syncPath: String,
    @Autowired(required = false)
    private val changeDetectorService: DatabaseChangeDetectorService?
) : HealthIndicator {

    override fun health(): Health {
        val targetFile = File(syncPath)
        val parentDir = targetFile.parentFile

        val builder = if (parentDir != null && parentDir.exists() && !parentDir.canWrite()) {
            Health.down().withDetail("reason", "Sync target parent directory is not writable")
        } else {
            Health.up()
        }

        builder.withDetail("syncPath", targetFile.absolutePath)
            .withDetail("fileExists", targetFile.exists())
            .withDetail("fileSizeBytes", if (targetFile.exists()) targetFile.length() else 0L)
            .withDetail("lastSignature", changeDetectorService?.getCurrentSignature() ?: "N/A")
            .withDetail("lastSyncTimestamp", changeDetectorService?.getLastSyncTimestamp()?.toString() ?: "N/A")

        return builder.build()
    }
}
