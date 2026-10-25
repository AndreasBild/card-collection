package de.maulmann.cardcollection.config

import de.maulmann.cardcollection.model.FeaturedPlayer
import jakarta.annotation.PostConstruct
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

/** `collection.owner-name`: whose collection this is (page titles, JSON-LD, export labels, player ordering). */
@ConfigurationProperties(prefix = "collection")
data class CollectionProperties(val ownerName: String = FeaturedPlayer.DEFAULT_NAME)

@Configuration
@EnableConfigurationProperties(CollectionProperties::class)
class CollectionConfig(private val properties: CollectionProperties) {

    @PostConstruct
    fun publishOwnerName() {
        FeaturedPlayer.name = properties.ownerName.trim().ifEmpty { FeaturedPlayer.DEFAULT_NAME }
    }
}
