package ch.frankel.conf.automation.action

import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.slf4j.LoggerFactory
import org.springframework.web.client.RestTemplate

class RemoveDueDate(private val template: RestTemplate) : JavaDelegate {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun execute(execution: DelegateExecution) {
        logger.debug("[${execution.processInstanceId}] Start action of removing due date")
        val request = mapOf(
            "id" to execution.event.cardId,
            "value" to null).toEntity()
        template.put("/cards/${execution.event.cardId}/due?key={key}&token={token}", request)
        logger.debug("[${execution.processInstanceId}] Request to remove due date has been sent")
    }
}