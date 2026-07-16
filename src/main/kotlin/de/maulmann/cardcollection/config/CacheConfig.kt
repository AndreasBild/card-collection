package de.maulmann.cardcollection.config
import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit
@Configuration
@EnableCaching
class CacheConfig {
    @Bean
    fun cacheManager(): CacheManager {
        val cacheManager = CaffeineCacheManager()
        cacheManager.setCaffeine(
            Caffeine.newBuilder()
                .maximumSize(500)
                .expireAfterWrite(10, TimeUnit.MINUTES)
        )
        cacheManager.setCacheNames(
            listOf(
                "brands",
                "themes",
                "sports",
                "seasons",
                "variants",
                "teams",
                "manufacturers",
                "players",
                "filteredCards"
            )
        )
        return cacheManager
    }
}