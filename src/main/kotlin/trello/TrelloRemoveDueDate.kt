package ch.frankel.conf.automation.trello

import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.util.Loggers

class TrelloRemoveDueDate(private val client: WebClient) {

    private val logger = Loggers.getLogger(this::class.java)

    fun execute(processInstanceId: String, cardId: String) {
        logger.debug("[$processInstanceId] Start action of removing due date")
        val request = mapOf(
            "id" to cardId,
            "value" to null
        )
        client.put()
            .uri("/cards/$cardId/due?key={key}&token={token}")
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .toBodilessEntity()
            .block()
        logger.debug("[$processInstanceId] Request to remove due date has been sent")
    }
}
