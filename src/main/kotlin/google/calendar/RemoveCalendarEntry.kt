package ch.frankel.conf.automation.google.calendar

import ch.frankel.conf.automation.AppProperties
import ch.frankel.conf.automation.conference
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate

class RemoveCalendarEntry(props: AppProperties) : JavaDelegate {

    private val client = props.calendarClient
    private val google = props.google

    override fun execute(execution: DelegateExecution) {
        findCalendarEntry(client, google, execution.conference)?.let {
            client.events().delete(google.calendar.id, it.id).execute()
        }
    }
}