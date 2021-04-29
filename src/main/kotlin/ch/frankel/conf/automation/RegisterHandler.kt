package ch.frankel.conf.automation

import camundajar.impl.com.google.gson.Gson
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse
import reactor.util.Loggers

class RegisterHandler(props: AppProperties, builder: WebClient.Builder) {

    private val trello = props.trello
    private val logger = Loggers.getLogger(this::class.java)
    private val client = builder.build()

    fun post(request: ServerRequest): ServerResponse {
        logger.info("Received registration request from ${request.remoteAddress()}")
        val requestEntity = mapOf(
            "idModel" to trello.boardId,
            "callbackURL" to request.callbackUrl,
            "description" to "Conference workflow automation"
        )
        client.post()
            .uri("/tokens/{token}/webhooks?key={key}")
            .body(BodyInserters.fromValue(requestEntity))
            .retrieve()
            .bodyToMono(RegisterResponse::class.java)
            .block()
            .also {
                logger.info("Registration response from Trello: $it")
                return ServerResponse.accepted()
                    .body(it.toJson())
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