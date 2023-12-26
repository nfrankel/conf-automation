package ch.frankel.conf.automation.google.calendar

import ch.frankel.conf.automation.CalendarProperties
import ch.frankel.conf.automation.action.Conference
import ch.frankel.conf.automation.conference
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventDateTime
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AddCalendarEntry(private val client: Calendar, private val props: CalendarProperties) : JavaDelegate {

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun execute(execution: DelegateExecution) {
        val entry = findCalendarEntry(client, props, execution.conference)
        if (entry == null) {
            val event = execution.conference.toCalendarEvent()
            client.events().insert(props.id, event).execute()
            logger.info("[${execution.processInstanceId}] Calendar entry added for ${execution.conference.name}")
        } else {
            logger.error("[${execution.processInstanceId}] Calendar entry already exists for ${execution.conference.name}")
        }
    }

    private fun Conference.toCalendarEvent() = Event().apply {
        summary = "${this@toCalendarEvent.name} (${this@toCalendarEvent.location})"
        val startDate = this@toCalendarEvent.startDate
        val endDate = this@toCalendarEvent.endDate
        start = startDate.toEventDateTime()
        end = if (startDate.isEqual(endDate))
            endDate.toEventDateTime()
        else
            endDate.plusDays(1).toEventDateTime()
        colorId = Color.Gray.id
        transparency = Availability.Free.value
    }

    private fun LocalDate.toEventDateTime() = EventDateTime().apply {
        date = DateTime(formatter.format(this@toEventDateTime))
    }
}
