package ch.frankel.conf.automation.action

import ch.frankel.conf.automation.AppProperties
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.http.*
import org.springframework.web.client.RestTemplate
import kotlin.collections.List

class ExtractConference(private val props: AppProperties,
                        private val fieldsInitializer: CustomFieldsInitializer): JavaDelegate {

    override fun execute(execution: DelegateExecution) {
        val fields = execution.customFields
        val name = execution.event.action.data.card.name
        execution.setVariable("conference", Conference(name, fields))
    }

    private val DelegateExecution.customFields: List<Field<out Any>>
        get() {
            val params = mapOf("id" to event.cardId,
                "key" to props.trello.key,
                "token" to props.trello.token)
            val response = RestTemplate().exchange(
                "https://api.trello.com/1/cards/{id}/customFieldItems?key={key}&token={token}",
                HttpMethod.GET,
                HttpEntity<Any>(HttpHeaders()),
                typeRef<List<CustomFieldItem>>(),
                params
            )
            return response.body.orEmpty().map {
                val fieldName = fieldsInitializer.fields?.get(it.idCustomField)
                when {
                    it.value?.text != null && fieldName == "Site" -> Website(it)
                    it.value?.date != null && fieldName == "Start date" -> StartDate(it)
                    it.value?.date != null && fieldName == "End date" -> EndDate(it)
                    else -> IrrelevantField
                }
            }.filter { it !is IrrelevantField }
        }
}