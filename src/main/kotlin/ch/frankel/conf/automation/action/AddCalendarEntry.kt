package ch.frankel.conf.automation.action

import ch.frankel.conf.automation.AppProperties
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventDateTime
import org.camunda.bpm.engine.delegate.DelegateExecution
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AddCalendarEntry(private val props: AppProperties, fieldsInitializer: CustomFieldsInitializer) : GoogleAction(props, fieldsInitializer) {

    override fun execute(execution: DelegateExecution) {
        val client = Calendar
            .Builder(TRANSPORT, JSON_FACTORY, credential)
            .build()
        val event = extractConference(execution).toCalendarEvent()
        client.events().insert(props.google.calendarId, event).execute()
    }

    private fun Conference.toCalendarEvent() = Event().apply {
        summary = "${this@toCalendarEvent.name} (${this@toCalendarEvent.location})"
        start = this@toCalendarEvent.startDate.toEventDateTime()
        end = this@toCalendarEvent.endDate.toEventDateTime()
        transparency = "transparent"
    }
}

private fun LocalDate.toEventDateTime(): EventDateTime {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return EventDateTime().apply {
        date = DateTime(formatter.format(this@toEventDateTime))
    }
}