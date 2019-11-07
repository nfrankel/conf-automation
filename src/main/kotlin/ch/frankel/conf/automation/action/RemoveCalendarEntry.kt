package ch.frankel.conf.automation.action

import ch.frankel.conf.automation.AppProperties
import com.google.api.services.calendar.Calendar
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate

class RemoveCalendarEntry(private val props: AppProperties) : JavaDelegate {

    override fun execute(execution: DelegateExecution) {
        val client = Calendar
            .Builder(TRANSPORT, JSON_FACTORY, props.toCredential())
            .build()
        findCalendarEntry(client, props.google, execution.conference)?.let {
            client.events().delete(props.google.calendarId, it.id).execute()
        }
    }
}