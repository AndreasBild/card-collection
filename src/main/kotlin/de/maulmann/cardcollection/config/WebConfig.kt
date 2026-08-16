package de.maulmann.cardcollection.config

import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.filter.ShallowEtagHeaderFilter

@Configuration
class WebConfig {

    @Bean
    fun shallowEtagHeaderFilter(): FilterRegistrationBean<ShallowEtagHeaderFilter> {
        val filterRegistrationBean = FilterRegistrationBean(ShallowEtagHeaderFilter())
        filterRegistrationBean.addUrlPatterns("/*")
        filterRegistrationBean.setName("etagFilter")
        filterRegistrationBean.order = 2
        return filterRegistrationBean
    }
}
