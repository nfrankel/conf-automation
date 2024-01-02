package ch.frankel.conf.automation.delegate.trello

import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientResponseException

class TickDueDateDelegate(private val client: RestClient) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun execute(processInstanceId: String, cardId: String, tick: Boolean) {
        val what = if (tick) "tick" else "untick"
        logger.info("[$processInstanceId] Start ${what}ing due date on card $cardId")
        try {
            val response = client.put()
                .uri("/cards/$cardId/dueComplete?key={key}&token={token}")
                .contentType(MediaType.APPLICATION_JSON)
                .body(mapOf("value" to tick))
                .retrieve()
                .toBodilessEntity()
            logger.info("[$processInstanceId] Due date $what result on card $cardId is ${response.statusCode}")
        } catch (e: RestClientResponseException) {
            logger.info("[$processInstanceId] Due date $tick failed on card $cardId with status ${e.statusCode}. Most likely the card has no due date")
        }
    }
}
