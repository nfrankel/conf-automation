package ch.frankel.conf.automation.action

import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.util.Loggers

class RemoveDueDate(builder: WebClient.Builder) : JavaDelegate {

    private val client = builder.build()
    private val logger = Loggers.getLogger(this::class.java)

    override fun execute(execution: DelegateExecution) {
        logger.debug("[${execution.processInstanceId}] Start action of removing due date")
        val request = mapOf(
            "id" to execution.event.cardId,
            "value" to null
        )
        client.put()
            .uri("/cards/${execution.event.cardId}/due?key={key}&token={token}")
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .toBodilessEntity()
            .block()
        logger.debug("[${execution.processInstanceId}] Request to remove due date has been sent")
    }
}