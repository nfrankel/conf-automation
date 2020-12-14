package ch.frankel.conf.automation

import inet.ipaddr.IPAddress
import inet.ipaddr.IPAddressString
import org.slf4j.LoggerFactory
import org.springframework.boot.web.servlet.FilterRegistrationBean
import javax.servlet.Filter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.collections.List


class WhitelistIPFilterRegistrationBean(props: AppProperties) : FilterRegistrationBean<Filter>() {

    private val logger = LoggerFactory.getLogger(WhitelistIPFilterRegistrationBean::class.java)
    private val ips: List<IPAddress> by lazy {
        props.trello.ips.map { IPAddressString(it).address }
    }

    init {
        filter = Filter { request, response, chain ->
            val remoteAddress = (request as HttpServletRequest).remoteAddr
            logger.info("Request received from $remoteAddress")
            if (ips.none { it.contains(IPAddressString(remoteAddress).address) } ) {
                logger.info("IP $remoteAddress is not white-listed, denying")
                (response as HttpServletResponse).status = 403
            } else chain.doFilter(request, response)
        }
        addUrlPatterns("/trigger")
    }
}