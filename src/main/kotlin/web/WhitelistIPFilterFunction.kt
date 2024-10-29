package ch.frankel.conf.automation.web

import ch.frankel.conf.automation.AppProperties
import inet.ipaddr.IPAddressString
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.web.servlet.function.HandlerFilterFunction
import org.springframework.web.servlet.function.HandlerFunction
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse


class WhitelistIPFilterFunction(props: AppProperties) : HandlerFilterFunction<ServerResponse, ServerResponse> {

    private val logger = LoggerFactory.getLogger(this::class.java)
    private val ips = props.trello.ips.map { IPAddressString(it).address }

    override fun filter(request: ServerRequest, next: HandlerFunction<ServerResponse>): ServerResponse {
        val remoteAddress: String? = request.remoteAddress().orElse(null)?.address?.hostAddress
        logger.info("Request received from $remoteAddress")
        return if (remoteAddress == null) {
            logger.warn("Remote address is null, denying access")
            ServerResponse.status(FORBIDDEN).build()
        } else if (ips.none { it.contains(IPAddressString(remoteAddress).address) }) {
            logger.info("IP $remoteAddress is not white-listed, denying access")
            ServerResponse.status(FORBIDDEN).build()
        } else {
            logger.info("IP $remoteAddress is white-listed, allowing access")
            next.handle(request)
        }
    }
}
