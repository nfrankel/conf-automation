package ch.frankel.conf.automation

import ch.frankel.conf.automation.ActionType.*
import ch.frankel.conf.automation.facade.Conference
import kotlinx.serialization.json.Json
import org.camunda.bpm.engine.delegate.DelegateExecution

internal const val BPMN_CONFERENCE = "conference"

enum class Message {
    Created, Submitted, Accepted, Refused, Abandoned, Irrelevant, Backlog, Published;

    companion object {

        private operator fun invoke(name: String?) = try {
            Message.valueOf(name ?: "Irrelevant")
        } catch (e: IllegalArgumentException) {
            Irrelevant
        }

        internal fun from(action: Action) = when (action.type) {
            COPY_CARD, CREATE_CARD -> Created
            UPDATE_CUSTOM_FIELD_ITEM -> Irrelevant
            ADD_LABEL_TO_CARD -> Irrelevant
            UPDATE_CARD -> {
                val before = action.data.listBefore
                val after = action.data.listAfter
                // Transition from one state to another
                if (before?.name != after?.name) Message(after?.name)
                // Anything else
                Irrelevant
            }
        }
    }
}

internal fun DelegateExecution.getConference(): Conference {
    val conferenceAsJson = getVariable(BPMN_CONFERENCE) as String
    return Json.decodeFromString<Conference>(conferenceAsJson)
}
