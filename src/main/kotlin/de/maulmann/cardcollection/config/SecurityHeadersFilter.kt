package de.maulmann.cardcollection.config

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class SecurityHeadersFilter : Filter {

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        if (response is HttpServletResponse) {
            response.setHeader("X-Content-Type-Options", "nosniff")
            response.setHeader("X-Frame-Options", "SAMEORIGIN")
            response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin")
            response.setHeader(
                "Content-Security-Policy",
                "default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline'; img-src 'self' data:; font-src 'self' data:; connect-src 'self'; frame-ancestors 'self'; form-action 'self';"
            )
            response.setHeader("Permissions-Policy", "camera=(), microphone=(), geolocation=(), payment=()")
            response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains")
        }
        chain.doFilter(request, response)
    }
}
