package ch.frankel.conf.automation.action

import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestTemplate

class ExtractConference(private val fieldsInitializer: CustomFieldsInitializer,
                        private val template: RestTemplate) : JavaDelegate {

    companion object {
        const val BPMN_CONFERENCE = "conference"
    }

    override fun execute(execution: DelegateExecution) {
        val fields = execution.customFields
        val name = execution.event.action.data.card.name
        execution.setVariable(BPMN_CONFERENCE, Conference(name, fields))
    }

    private val DelegateExecution.customFields: List<Field<out Any>>
        get() = template.exchange(
            "/cards/{id}/customFieldItems?key={key}&token={token}",
            HttpMethod.GET,
            HttpEntity.EMPTY,
            typeRef<List<CustomFieldItem>>(),
            mapOf("id" to event.cardId)
        ).body.orEmpty()
            .map {
                val fieldName = fieldsInitializer.fields[it.idCustomField]
                when {
                    it.value?.text != null && fieldName == "Site" -> Website(it)
                    it.value?.date != null && fieldName == "Start date" -> StartDate(it)
                    it.value?.date != null && fieldName == "End date" -> EndDate(it)
                    else -> IrrelevantField
                }
            }.filter { it !is IrrelevantField }
}