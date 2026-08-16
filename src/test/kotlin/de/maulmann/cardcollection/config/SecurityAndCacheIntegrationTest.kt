package de.maulmann.cardcollection.config

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cache.CacheManager
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.mock.web.MockFilterChain
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class SecurityAndCacheIntegrationTest {

    @Autowired
    private lateinit var cacheManager: CacheManager

    @Autowired
    private lateinit var securityHeadersFilter: SecurityHeadersFilter

    @Autowired
    private lateinit var exportRateLimiter: ExportRateLimiter

    @Test
    fun `test security headers are correctly applied`() {
        val request = MockHttpServletRequest("GET", "/cards")
        val response = MockHttpServletResponse()
        val filterChain = MockFilterChain()

        securityHeadersFilter.doFilter(request, response, filterChain)

        assertThat(response.getHeader("X-Content-Type-Options")).isEqualTo("nosniff")
        assertThat(response.getHeader("X-Frame-Options")).isEqualTo("SAMEORIGIN")
        assertThat(response.getHeader("Referrer-Policy")).isEqualTo("strict-origin-when-cross-origin")
        assertThat(response.getHeader("Content-Security-Policy")).contains("default-src 'self'")
        assertThat(response.getHeader("Permissions-Policy")).contains("geolocation=()")
        assertThat(response.getHeader("Strict-Transport-Security")).contains("max-age=31536000")
    }

    @Test
    fun `test cache manager registers domain-specific caches`() {
        assertThat(cacheManager).isInstanceOf(CaffeineCacheManager::class.java)

        val referenceCaches = listOf("sports", "seasons", "manufacturers", "teams", "brands", "themes", "variants", "players", "filteredCards")
        for (name in referenceCaches) {
            val cache = cacheManager.getCache(name)
            assertThat(cache).isNotNull
        }
    }

    @Test
    fun `test export rate limiter allows normal traffic`() {
        val request = MockHttpServletRequest("GET", "/export/json")
        val response = MockHttpServletResponse()
        val filterChain = MockFilterChain()

        exportRateLimiter.doFilter(request, response, filterChain)

        assertThat(response.status).isEqualTo(200)
    }
}
