package ch.frankel.conf.automation.action

import ch.frankel.conf.automation.AppProperties
import com.google.api.services.calendar.Calendar
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate

class UpdateCalendarEntry(private val props: AppProperties) : JavaDelegate {

    override fun execute(execution: DelegateExecution) {
        val client = Calendar
            .Builder(TRANSPORT, JSON_FACTORY, props.credential)
            .setApplicationName(props.name)
            .build()
        findCalendarEntry(client, props.google, execution.conference)?.let {
            it.transparency = "opaque"
            client.events()
                .update(props.google.calendarId, it.id, it).execute()
        }
    }
}