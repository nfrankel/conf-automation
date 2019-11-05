package ch.frankel.conf.automation.action

import ch.frankel.conf.automation.AppProperties
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.slf4j.LoggerFactory
import org.springframework.web.client.RestTemplate

private val LOGGER = LoggerFactory.getLogger("ch.frankel.conf.automation.action.RemoveDueDate")

class RemoveDueDate(private val props: AppProperties) : JavaDelegate {

    override fun execute(execution: DelegateExecution) {
        LOGGER.debug("[${execution.processInstanceId}] Start action of removing due date")
        val params = mapOf("id" to execution.event.cardId,
            "key" to props.trello.key,
            "token" to props.trello.token)
        RestTemplate().put(
            "https://api.trello.com/1/cards/{id}/due?key={key}&token={token}",
            "{ 'value': null }",
            params)
        LOGGER.debug("[${execution.processInstanceId}] Request to remove due date has been sent")
    }
}