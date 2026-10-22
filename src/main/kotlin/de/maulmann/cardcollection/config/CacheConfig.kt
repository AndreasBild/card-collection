package de.maulmann.cardcollection.config

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.Page
import java.util.concurrent.TimeUnit

@Configuration
@EnableCaching
class CacheConfig {

    @Bean
    fun cacheManager(): CacheManager {
        val caffeineCacheManager = CaffeineCacheManager()

        // Reference lookup caches (rarely mutate -> 24 hours TTL)
        val referenceCaches = listOf("sports", "seasons", "manufacturers", "teams", "brands", "themes", "variants")
        for (cacheName in referenceCaches) {
            caffeineCacheManager.registerCustomCache(
                cacheName,
                Caffeine.newBuilder()
                    .maximumSize(500)
                    .expireAfterWrite(24, TimeUnit.HOURS)
                    .recordStats()
                    .build()
            )
        }

        // Players cache (12 hours TTL, 1000 entries)
        caffeineCacheManager.registerCustomCache(
            "players",
            Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(12, TimeUnit.HOURS)
                .recordStats()
                .build()
        )

        // Filtered card results (dynamic query cache: 30 mins TTL). Bounded by total cards held
        // (weight = page size) rather than page count, so one large "size=all" page cannot exhaust memory.
        caffeineCacheManager.registerCustomCache(
            "filteredCards",
            Caffeine.newBuilder()
                .maximumWeight(20_000)
                .weigher<Any, Any> { _, value -> (value as? Page<*>)?.numberOfElements?.coerceAtLeast(1) ?: 1 }
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .recordStats()
                .build()
        )

        // Fallback default specification for any other cache
        caffeineCacheManager.setCaffeine(
            Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(1, TimeUnit.HOURS)
                .recordStats()
        )

        return caffeineCacheManager
    }
}