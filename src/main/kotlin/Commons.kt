package ch.frankel.conf.automation

import ch.frankel.conf.automation.action.BPMN_CONFERENCE
import ch.frankel.conf.automation.action.Conference
import org.camunda.bpm.engine.delegate.DelegateExecution

internal val DelegateExecution.conference: Conference
    get() = getVariable(BPMN_CONFERENCE) as Conference
