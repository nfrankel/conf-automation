package ch.frankel.conf.automation.action

import ch.frankel.conf.automation.TrelloEvent
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.springframework.core.ParameterizedTypeReference

internal val TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()
internal val JSON_FACTORY = JacksonFactory.getDefaultInstance()

internal inline fun <reified T : Any> typeRef(): ParameterizedTypeReference<T> = object : ParameterizedTypeReference<T>() {}

internal val DelegateExecution.event: TrelloEvent
    get() = getVariable("event") as TrelloEvent

internal val TrelloEvent.cardId: String
    get() = this.action.data.card.id