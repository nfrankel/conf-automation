package ch.frankel.conf.automation.action

import ch.frankel.conf.automation.TrelloEvent
import ch.frankel.conf.automation.TriggerHandler.Companion.BPMN_EVENT
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import org.camunda.bpm.engine.delegate.DelegateExecution

internal val TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()
internal val JSON_FACTORY = GsonFactory.getDefaultInstance()

internal val DelegateExecution.event: TrelloEvent
    get() = getVariable(BPMN_EVENT) as TrelloEvent

internal val TrelloEvent.cardId: String
    get() = this.action.data.card.id