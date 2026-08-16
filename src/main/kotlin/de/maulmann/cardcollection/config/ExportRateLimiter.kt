package de.maulmann.cardcollection.config

import com.github.benmanes.caffeine.cache.Caffeine
import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
class ExportRateLimiter : Filter {

    companion object {
        private const val MAX_CONCURRENT_EXPORTS = 10
        private const val MAX_REQUESTS_PER_MINUTE = 20
    }

    private val concurrentExportSemaphore = Semaphore(MAX_CONCURRENT_EXPORTS)

    private val ipRequestCounts = Caffeine.newBuilder()
        .expireAfterWrite(1, TimeUnit.MINUTES)
        .maximumSize(5_000)
        .build<String, AtomicInteger>()

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        if (request is HttpServletRequest && response is HttpServletResponse) {
            val uri = request.requestURI
            if (uri.startsWith("/export")) {
                val clientIp = extractClientIp(request)
                val count = ipRequestCounts.asMap().computeIfAbsent(clientIp) { AtomicInteger(0) }.incrementAndGet()

                if (count > MAX_REQUESTS_PER_MINUTE) {
                    response.status = 429
                    response.contentType = "text/plain;charset=UTF-8"
                    response.writer.write("Too many export requests. Please try again in a minute.")
                    return
                }

                if (!concurrentExportSemaphore.tryAcquire(5, TimeUnit.SECONDS)) {
                    response.status = 503
                    response.contentType = "text/plain;charset=UTF-8"
                    response.writer.write("Export service is currently busy. Please try again shortly.")
                    return
                }

                try {
                    chain.doFilter(request, response)
                } finally {
                    concurrentExportSemaphore.release()
                }
                return
            }
        }
        chain.doFilter(request, response)
    }

    private fun extractClientIp(request: HttpServletRequest): String {
        val forwarded = request.getHeader("X-Forwarded-For")
        return if (!forwarded.isNullOrBlank()) {
            forwarded.split(",")[0].trim()
        } else {
            request.remoteAddr ?: "unknown"
        }
    }
}
