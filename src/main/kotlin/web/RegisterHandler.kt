package ch.frankel.conf.automation.web

import camundajar.impl.com.google.gson.Gson
import ch.frankel.conf.automation.AppProperties
import org.slf4j.LoggerFactory
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestClient
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse

class RegisterHandler(props: AppProperties, private val client: RestClient) {

    private val trello = props.trello
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun post(request: ServerRequest): ServerResponse {
        logger.info("Received registration request from ${request.remoteAddress()}")
        val requestEntity = mapOf(
            "idModel" to trello.boardId,
            "callbackURL" to request.callbackUrl,
            "description" to "Conference workflow automation"
        )
        return try {
            val response = client.post()
                .uri("/tokens/{token}/webhooks?key={key}")
                .body(requestEntity)
                .retrieve()
                .body(RegisterResponse::class.java)
            logger.info("Registration response from Trello: $response")
            ServerResponse.accepted().body(response.toJson())
        } catch (e: HttpClientErrorException) {
            logger.error("Failed to register webhook with Trello", e)
            ServerResponse.status(e.statusCode).body(e.responseBodyAsString)
        } catch (e: Exception) {
            logger.error("Unexpected error during registration", e)
            ServerResponse.status(500).body("Internal Server Error")
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
            val uri = uri()
            return "${uri.scheme}://${uri.host}:${uri.port}/trigger"
        }

    private fun Any?.toJson() = Gson().toJson(this)
}
