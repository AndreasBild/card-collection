package de.maulmann.cardcollection.config

import jakarta.servlet.http.HttpServletRequest
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.filter.ShallowEtagHeaderFilter

@Configuration
class WebConfig {

    class SelectiveEtagFilter : ShallowEtagHeaderFilter() {
        override fun shouldNotFilter(request: HttpServletRequest): Boolean {
            val uri = request.requestURI
            return uri.startsWith("/export/") || uri == "/export"
        }
    }

    @Bean
    fun shallowEtagHeaderFilter(): FilterRegistrationBean<ShallowEtagHeaderFilter> {
        val filterRegistrationBean = FilterRegistrationBean<ShallowEtagHeaderFilter>(SelectiveEtagFilter())
        filterRegistrationBean.addUrlPatterns("/*")
        filterRegistrationBean.setName("etagFilter")
        filterRegistrationBean.order = 2
        return filterRegistrationBean
    }
}
