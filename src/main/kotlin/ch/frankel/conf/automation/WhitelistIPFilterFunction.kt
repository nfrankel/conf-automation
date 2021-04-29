package ch.frankel.conf.automation

import inet.ipaddr.IPAddress
import inet.ipaddr.IPAddressString
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.web.servlet.function.*
import reactor.util.Loggers


class WhitelistIPFilterFunction(props: AppProperties) : HandlerFilterFunction<ServerResponse, ServerResponse> {

    private val logger = Loggers.getLogger(this::class.java)
    private val ips: kotlin.collections.List<IPAddress> by lazy {
        props.trello.ips.map { IPAddressString(it).address }
    }

    override fun filter(request: ServerRequest, next: HandlerFunction<ServerResponse>): ServerResponse {
        val remoteAddress: String? = request.remoteAddress().orElse(null)?.address?.hostAddress
        logger.info("Request received from $remoteAddress")
        return if (ips.none { it.contains(IPAddressString(remoteAddress).address) }) {
            logger.info("IP $remoteAddress is not white-listed, denying")
            ServerResponse.status(FORBIDDEN).build()
        } else next.handle(request)
    }
}