package ch.frankel.conf.automation

import com.google.gson.Gson
import org.slf4j.LoggerFactory
import org.springframework.http.*
import org.springframework.web.client.RestTemplate
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse

class RegisterHandler(props: AppProperties) {

    private val trello = props.trello
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun post(request: ServerRequest): ServerResponse {
        logger.info("Received registration request from ${request.remoteAddress()}")
        return with(RestTemplate().exchange(
            "https://api.trello.com/1/tokens/{token}/webhooks",
            HttpMethod.POST,
            RegisterRequest(
                trello.key,
                trello.boardId,
                request.callbackUrl,
                "Conference workflow automation"
            ).toHttpEntity(),
            RegisterResponse::class.java,
            mapOf("token" to trello.token))) {
            logger.info("Registration response from Trello: $body")
            ServerResponse.accepted().body(body.toJson())
        }
    }

    private data class RegisterRequest(
        val key: String,
        val idModel: String,
        val callbackURL: String,
        val description: String
    )

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

    private fun RegisterRequest.toHttpEntity() = HttpEntity(
        toJson(),
        HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }
    )

    private fun Any?.toJson() = Gson().toJson(this)
}