package ch.frankel.conf.automation

import ch.frankel.conf.automation.action.event
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.slf4j.LoggerFactory

const val BPMN_STATUS = "status"

enum class Status {
    Submitted,
    Accepted,
    Rejected,
    Abandoned,
    Irrelevant
}

internal val computeEventStatus = JavaDelegate {
    LOGGER.info("[${it.processInstanceId}] Received event ${it.event}")
    with(it.event.status) {
        it.setVariable(BPMN_STATUS, this)
        LOGGER.info("[${it.processInstanceId}] Event status is ${this}")
    }
}

private val LOGGER = LoggerFactory.getLogger("ch.frankel.conf.automation.Status")

private val eventStatusMatrix = mapOf(
    ("Backlog" to "Submitted") to Status.Submitted,
    ("Submitted" to "Accepted") to Status.Accepted,
    ("Submitted" to "Refused") to Status.Rejected,
    ("Backlog" to "Abandoned") to Status.Abandoned
)

private val TrelloEvent.status: Status
    get() {
        val before = action.data.listBefore
        val after = action.data.listAfter
        val key = before?.name to after?.name
        return eventStatusMatrix.getOrDefault(key, Status.Irrelevant)
    }