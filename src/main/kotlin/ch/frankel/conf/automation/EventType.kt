package ch.frankel.conf.automation

import ch.frankel.conf.automation.action.event
import ch.frankel.conf.automation.trigger.TrelloEvent
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.slf4j.LoggerFactory

enum class EventType {
    Submission,
    Acceptance,
    Rejection,
    Abandoned,
    Irrelevant
}

internal val computeEventType = JavaDelegate {
    LOGGER.info("[${it.processInstanceId}] Received event ${it.event}")
    with(it.event.type) {
        it.setVariable("type", this)
        LOGGER.info("[${it.processInstanceId}] Event type is ${this}")
    }
}

private val LOGGER = LoggerFactory.getLogger("ch.frankel.conf.automation.ComputeEventType")

private val TrelloEvent.type: EventType
    get() {
        val before = action.data.listBefore
        val after = action.data.listAfter
        if (before == null || after == null) return EventType.Irrelevant
        if (before.name == "Backlog")
            return when (after.name) {
                "Submitted" -> EventType.Submission
                "Accepted" -> EventType.Acceptance
                "Rejected" -> EventType.Rejection
                "Abandoned" -> EventType.Abandoned
                else -> EventType.Irrelevant
            }
        return EventType.Irrelevant
    }