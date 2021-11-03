package ch.frankel.conf.automation.action

import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class ExtractConference(
    private val fieldsInitializer: CustomFieldsInitializer,
    private val client: WebClient
) : JavaDelegate {

    companion object {
        const val BPMN_CONFERENCE = "conference"
    }

    override fun execute(execution: DelegateExecution) {
        val fields = execution.customFields.collectList().block() ?: emptyList()
        val name = execution.event.action.data.card.name
        execution.setVariable(BPMN_CONFERENCE, Conference(name, fields))
    }

    private val DelegateExecution.customFields: Flux<Field<out Any>>
        get() = client.get()
            .uri("/cards/${event.cardId}/customFieldItems?key={key}&token={token}")
            .retrieve()
            .bodyToFlux(CustomFieldItem::class.java)
            .flatMap { getField(it) }
            .filter { it !is IrrelevantField }

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