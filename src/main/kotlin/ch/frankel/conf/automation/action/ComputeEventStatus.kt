package ch.frankel.conf.automation.action

import ch.frankel.conf.automation.TrelloEvent
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.slf4j.LoggerFactory

class ComputeEventStatus : JavaDelegate {

    override fun execute(it: DelegateExecution) {
        val logger = LoggerFactory.getLogger("ch.frankel.conf.automation.action.Status")
        logger.info("[${it.processInstanceId}] Received event ${it.trelloEvent}")
        with(it.trelloEvent.status) {
            it.setVariable(BPMN_STATUS, this)
            logger.info("[${it.processInstanceId}] Event status is $this")
        }
    }

    private val eventStatusMatrix = mapOf(
        ("Backlog" to "Submitted") to Status.Submitted,
        ("Submitted" to "Accepted") to Status.Accepted,
        ("Submitted" to "Refused") to Status.Refused,
        ("Backlog" to "Abandoned") to Status.Abandoned
    )

    private val TrelloEvent.status: Status
        get() {
            val before = action.data.listBefore
            val after = action.data.listAfter
            val key = before?.name to after?.name
            return eventStatusMatrix.getOrDefault(key, Status.Irrelevant)
        }
}
