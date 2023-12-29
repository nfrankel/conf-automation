package ch.frankel.conf.automation.trello

import org.springframework.http.MediaType
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.util.Loggers

class TrelloTickDueDate(private val client: WebClient) {

    private val logger = Loggers.getLogger(this::class.java)

    fun execute(processInstanceId: String, cardId: String, tick: Boolean) {
        val what = if (tick) "tick" else "untick"
        logger.info("[$processInstanceId] Start ${what}ing due date on card $cardId")
        try {
            val response = client.put()
                .uri("/cards/$cardId/dueComplete?key={key}&token={token}")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(mapOf("value" to tick)))
                .retrieve()
                .toBodilessEntity()
                .block()
            logger.info("[$processInstanceId] Due date $what result on card $cardId is ${response?.statusCode}")
        } catch (e: WebClientResponseException) {
            logger.info("[$processInstanceId] Due date $tick failed on card $cardId with status ${e.statusCode}. Most likely the card has no due date")
        }
    }
}
