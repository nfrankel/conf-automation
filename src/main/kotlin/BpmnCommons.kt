package ch.frankel.conf.automation

import ch.frankel.conf.automation.facade.Conference
import kotlinx.serialization.json.Json
import org.camunda.bpm.engine.delegate.DelegateExecution

internal const val BPMN_CONFERENCE = "conference"

enum class Message {
    Created, Submitted, Accepted, Refused, Abandoned, Irrelevant, Backlog, Published;

    companion object {
        operator fun invoke(name: String?) = try {
            Message.valueOf(name ?: "Irrelevant")
        } catch (e: IllegalArgumentException) {
            Irrelevant
        }
    }
}

internal fun DelegateExecution.getConference(): Conference {
    val conferenceAsJson = getVariable(BPMN_CONFERENCE) as String
    return Json.decodeFromString<Conference>(conferenceAsJson)
}
