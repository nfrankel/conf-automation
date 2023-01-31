package ch.frankel.conf.automation

import ch.frankel.conf.automation.action.Conference
import org.camunda.bpm.engine.delegate.DelegateExecution

internal const val BPMN_CONFERENCE = "conference"

internal val DelegateExecution.conference: Conference
    get() = getVariable(BPMN_CONFERENCE) as Conference
