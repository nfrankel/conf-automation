package ch.frankel.conf.automation.action

import ch.frankel.conf.automation.AppProperties
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate

class UpdateCalendarEntry(props: AppProperties) : JavaDelegate {

    private val client = props.calendarClient
    private val google = props.google

    override fun execute(execution: DelegateExecution) {
        findCalendarEntry(client, google, execution.conference)?.let {
            it.colorId = Color.Purple.id
            it.transparency = Availability.Busy.value
            client.events()
                .update(google.calendarId, it.id, it).execute()
        }
    }
}