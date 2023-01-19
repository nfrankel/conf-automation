package ch.frankel.conf.automation.google.calendar

import ch.frankel.conf.automation.AppProperties
import ch.frankel.conf.automation.google.calendarClient
import ch.frankel.conf.automation.google.conference
import ch.frankel.conf.automation.google.findCalendarEntry
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate

class RemoveCalendarEntry(props: AppProperties) : JavaDelegate {

    private val client = props.calendarClient
    private val google = props.google

    override fun execute(execution: DelegateExecution) {
        findCalendarEntry(client, google, execution.conference)?.let {
            client.events().delete(google.calendarId, it.id).execute()
        }
    }
}