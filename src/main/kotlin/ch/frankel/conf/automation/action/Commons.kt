package ch.frankel.conf.automation.action

import ch.frankel.conf.automation.TrelloEvent
import org.camunda.bpm.engine.delegate.DelegateExecution

internal val DelegateExecution.trelloEvent: TrelloEvent
    get() = getVariable("event") as TrelloEvent
