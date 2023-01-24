package ch.frankel.conf.automation.google.calendar

import ch.frankel.conf.automation.AppProperties
import ch.frankel.conf.automation.action.*
import ch.frankel.conf.automation.conference
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventDateTime
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AddCalendarEntry(props: AppProperties) : JavaDelegate {

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val client = props.calendarClient
    private val google = props.google

    override fun execute(execution: DelegateExecution) {
        val entry = findCalendarEntry(client, google, execution.conference)
        if (entry == null) {
            val event = execution.conference.toCalendarEvent()
            client.events().insert(google.calendarId, event).execute()
        }
    }

    private fun Conference.toCalendarEvent() = Event().apply {
        summary = "${this@toCalendarEvent.name} (${this@toCalendarEvent.location})"
        start = this@toCalendarEvent.startDate.toEventDateTime()
        end = this@toCalendarEvent.endDate.toEventDateTime()
        colorId = Color.Gray.id
        transparency = Availability.Free.value
    }

    private fun LocalDate.toEventDateTime() = EventDateTime().apply {
        date = DateTime(formatter.format(this@toEventDateTime))
    }
}