package ch.frankel.conf.automation

import ch.frankel.conf.automation.action.toEntity
import com.google.gson.Gson
import org.slf4j.LoggerFactory
import org.springframework.web.client.RestTemplate
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse

class RegisterHandler(props: AppProperties, private val template: RestTemplate) {

    private val trello = props.trello
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun post(request: ServerRequest): ServerResponse {
        logger.info("Received registration request from ${request.remoteAddress()}")
        val requestEntity = mapOf(
            "idModel" to trello.boardId,
            "callbackURL" to request.callbackUrl,
            "description" to "Conference workflow automation").toEntity()
        return with(
            template.postForObject(
                "/tokens/{token}/webhooks?key={key}",
                requestEntity,
                RegisterResponse::class.java
            )
        ) {
            logger.info("Registration response from Trello: $this")
            ServerResponse.accepted().body(toJson())
        }
    }

    private data class RegisterResponse(
        var id: String,
        var description: String,
        var idModel: String,
        var callbackURL: String,
        var active: Boolean,
        var consecutiveFailures: Int
    )

    private val ServerRequest.callbackUrl: String
        get() {
            val servletRequest = servletRequest()
            return "${servletRequest.scheme}://${servletRequest.serverName}:${servletRequest.serverPort}/trigger"
        }

    private fun Any?.toJson() = Gson().toJson(this)
}