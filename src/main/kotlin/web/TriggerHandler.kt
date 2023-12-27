package ch.frankel.conf.automation.web

import ch.frankel.conf.automation.*
import ch.frankel.conf.automation.action.*
import org.camunda.bpm.engine.RuntimeService
import org.camunda.bpm.engine.runtime.MessageCorrelationResultType
import org.camunda.bpm.engine.runtime.MessageCorrelationResultType.Execution
import org.camunda.bpm.engine.runtime.MessageCorrelationResultType.ProcessDefinition
import org.slf4j.LoggerFactory
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse
import reactor.core.publisher.Mono


class TriggerHandler(
    private val runtimeService: RuntimeService,
    private val fieldsInitializer: CustomFieldsInitializer,
    private val client: WebClient,
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun post(request: ServerRequest): ServerResponse {
        val event = request.body(TrelloEvent::class.java)
        logger.info("Received event is $event")
        val transition = extractTransition(event)
        logger.info("Computed transition for ${event.action.data.card.name} is $transition")
        if (transition != IrrelevantChange) {
            val conference = extractConference(event)
            val params = mapOf(
                BPMN_CONFERENCE to conference,
                BPMN_TRANSITION to transition.toString(),
            )
            val result = runtimeService.createMessageCorrelation(transition.toString())
                .processInstanceBusinessKey(event.cardId)
                .setVariables(params)
                .correlateWithResult()
            return if (result.resultType == Execution) {
                logger.info("Started process instance with id ${result.execution.processInstanceId} for ${conference.name} with $transition")
                ServerResponse.accepted().body("{ id: ${result.execution.processInstanceId} }")
            } else {
                // result.resultType == ProcessDefinition
                logger.info("Existing process instance with id ${result.processInstance.processInstanceId} for ${conference.name} with $transition found. Continuing workflow")
                ServerResponse.accepted().body("{ id: ${result.processInstance.processInstanceId} }")
            }
        }
        return ServerResponse.noContent().build()
    }

    private fun extractTransition(event: TrelloEvent): CardChange {
        val before = event.action.data.listBefore
        val after = event.action.data.listAfter
        val key = before?.name to after?.name
        return CardChange.from(key)
    }

    private fun extractConference(event: TrelloEvent): Conference {
        val customFields: List<Field<out Any>> = getFieldsForCard(event.cardId)
        return Conference(event.action.data.card.name, customFields)
    }

    private fun getFieldsForCard(cardId: String) = client.get()
        .uri("/cards/$cardId/customFieldItems?key={key}&token={token}")
        .retrieve()
        .bodyToFlux(CustomFieldItem::class.java)
        .flatMap { getField(it) }
        .filter { it !is IrrelevantField }
        .collectList()
        .block() ?: emptyList()

    /* Required by Trello, as it executes a HEAD request to make sure the endpoint is up. */
    fun head(@Suppress("UNUSED_PARAMETER") request: ServerRequest) = ServerResponse.ok().build()

    private fun getField(item: CustomFieldItem): Mono<Field<out Any>> =
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
