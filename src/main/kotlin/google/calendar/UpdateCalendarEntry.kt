package ch.frankel.conf.automation.google.calendar

import ch.frankel.conf.automation.AppProperties
import ch.frankel.conf.automation.google.*
import ch.frankel.conf.automation.google.Availability
import ch.frankel.conf.automation.google.Color
import ch.frankel.conf.automation.google.calendarClient
import ch.frankel.conf.automation.google.findCalendarEntry
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate

class UpdateCalendarEntry(props: AppProperties) : JavaDelegate {

    private val client = props.calendarClient
    private val google = props.google

    override fun execute(execution: DelegateExecution) {
        findCalendarEntry(client, google, execution.conference)?.let {
            it.colorId = Color.Default.id
            it.transparency = Availability.Busy.value
            client.events()
                .update(google.calendarId, it.id, it).execute()
        }
    }
}