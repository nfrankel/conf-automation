package ch.frankel.conf.automation.web

import ch.frankel.conf.automation.*
import ch.frankel.conf.automation.facade.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.camunda.bpm.engine.RuntimeService
import org.camunda.bpm.engine.runtime.MessageCorrelationResultType.ProcessDefinition
import org.slf4j.LoggerFactory
import org.springframework.core.ParameterizedTypeReference
import org.springframework.web.client.RestClient
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse


class TriggerHandler(
    private val runtimeService: RuntimeService,
    private val fieldsInitializer: CustomFieldsInitializer,
    private val client: RestClient,
    private val eventSet: MutableSet<TrelloEvent>
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun post(request: ServerRequest): ServerResponse {
        val event = request.body(TrelloEvent::class.java)
        logger.info("Received event is ${Json.encodeToString(event)}")
        if (eventSet.contains(event)) {
            logger.info("Ignoring duplicate event $event")
            return ServerResponse.noContent().build()
        }
        eventSet.add(event)
        val message = Message.from(event.action)
        logger.info("Extracted message $message for ${event.action.data.card.name}")
        return when (message) {
            Message.Irrelevant, Message.Created -> {
                logger.info("Ignoring message $message for ${event.action.data.card.name}")
                ServerResponse.noContent().build()
            }
            else -> {
                val conference = extractConference(event)
                val params = mapOf(BPMN_CONFERENCE to Json.encodeToString(conference))
                val result = runtimeService.createMessageCorrelation(message.toString())
                    .processInstanceBusinessKey(event.cardId)
                    .setVariables(params)
                    .correlateWithResult()
                return if (result.resultType == ProcessDefinition) {
                    logger.info("[${result.processInstance.processInstanceId}] Started process instance with $message for ${conference.name}")
                    ServerResponse.accepted().body("{ id: ${result.processInstance.processInstanceId} }")
                } else {
                    logger.info("[${result.execution.processInstanceId}] Existing process instance found for ${conference.name}, continuing with $message")
                    ServerResponse.accepted().body("{ id: ${result.execution.processInstanceId} }")
                }
            }
        }
    }

    private fun extractConference(event: TrelloEvent): Conference {
        val customFields: List<Field<out Any>> = getFieldsForCard(event.cardId)
        return Conference.from(event.action.data.card.name, customFields)
    }

    private fun getFieldsForCard(cardId: String): List<Field<out Any>> = client.get()
        .uri("/cards/$cardId/customFieldItems?key={key}&token={token}")
        .retrieve()
        .body(object: ParameterizedTypeReference<List<CustomFieldItem>>() {})
        ?.map { getField(it) }
        ?.filter { it !is IrrelevantField } ?: listOf()

    /* Required by Trello, as it executes a HEAD request to make sure the endpoint is up. */
    fun head(request: ServerRequest) = ServerResponse.ok().build()

    private fun getField(item: CustomFieldItem): Field<out Any> =
        fieldsInitializer.fields
            .filter { it.first == item.idCustomField }
            .map {
                val name = it.second
                when {
                    item.value?.text != null && name == "Site" -> Website(item)
                    item.value?.date != null && name == "Start date" -> StartDate(item)
                    item.value?.date != null && name == "End date" -> EndDate(item)
                    else -> IrrelevantField
                }
            }.single()
}
