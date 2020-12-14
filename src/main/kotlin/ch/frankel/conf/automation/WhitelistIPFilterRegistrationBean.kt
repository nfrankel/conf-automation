package ch.frankel.conf.automation

import org.slf4j.LoggerFactory
import org.springframework.boot.web.servlet.FilterRegistrationBean
import javax.servlet.Filter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


class WhitelistIPFilterRegistrationBean(props: AppProperties) : FilterRegistrationBean<Filter>() {

    private val logger = LoggerFactory.getLogger(WhitelistIPFilterRegistrationBean::class.java)
    private val whitelist = props.trello.ips

    init {
        filter = Filter { request, response, chain ->
            val remoteAddress = (request as HttpServletRequest).remoteAddr
            logger.info("Request received from $remoteAddress")
            if (!whitelist.contains(remoteAddress)) {
                logger.info("IP $remoteAddress is not white-listed, denying")
                (response as HttpServletResponse).status = 403
            } else chain.doFilter(request, response)
        }
        addUrlPatterns("/trigger")
    }
}