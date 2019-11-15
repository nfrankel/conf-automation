package ch.frankel.conf.automation.action

import ch.frankel.conf.automation.TrelloEvent
import ch.frankel.conf.automation.TriggerHandler.Companion.BPMN_EVENT
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.util.CollectionUtils

internal val TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()
internal val JSON_FACTORY = JacksonFactory.getDefaultInstance()

internal inline fun <reified T : Any> typeRef(): ParameterizedTypeReference<T> = object : ParameterizedTypeReference<T>() {}

internal fun <K, V> Map<K, V>.toEntity(headers: HttpHeaders = HttpHeaders()) =
    HttpEntity(CollectionUtils.toMultiValueMap(
        map {
            if (it.value != null) it.key to listOf(it.value)
            else it.key to null
        }.toMap()
    ), headers)

internal val DelegateExecution.event: TrelloEvent
    get() = getVariable(BPMN_EVENT) as TrelloEvent

internal val TrelloEvent.cardId: String
    get() = this.action.data.card.id