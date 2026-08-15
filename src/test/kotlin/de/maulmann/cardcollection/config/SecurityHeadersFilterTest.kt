package de.maulmann.cardcollection.config

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class SecurityHeadersFilterTest {

    private val filter = SecurityHeadersFilter()

    @Test
    fun `test doFilter adds OWASP security headers`() {
        val request = MockHttpServletRequest()
        val response = MockHttpServletResponse()
        val filterChain = mock(FilterChain::class.java)

        filter.doFilter(request, response, filterChain)

        assertEquals("nosniff", response.getHeader("X-Content-Type-Options"))
        assertEquals("SAMEORIGIN", response.getHeader("X-Frame-Options"))
        assertEquals("strict-origin-when-cross-origin", response.getHeader("Referrer-Policy"))
        verify(filterChain).doFilter(request, response)
    }
}
