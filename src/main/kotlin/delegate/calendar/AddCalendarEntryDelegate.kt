package ch.frankel.conf.automation.delegate.calendar

import ch.frankel.conf.automation.CalendarProperties
import ch.frankel.conf.automation.action.Conference
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventDateTime
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import org.slf4j.LoggerFactory
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class AddCalendarEntryDelegate(private val client: Calendar, private val props: CalendarProperties) {

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault())
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun execute(conference: Conference) {
        val entry = findCalendarEntry(client, props, conference)
        if (entry == null) {
            val event = conference.toCalendarEvent()
            logger.info(event.toString())
            client.events().insert(props.id, event).execute()
            logger.info("Calendar entry added for ${conference.name}")
        } else {
            logger.error("Calendar entry already exists for ${conference.name}")
        }
    }

    private fun Conference.toCalendarEvent() = Event().apply {
        summary = "${this@toCalendarEvent.name} (${this@toCalendarEvent.location})"
        val startDate = this@toCalendarEvent.startDate
        val endDate = this@toCalendarEvent.endDate
        start = startDate.toEventDateTime()
        end = if (startDate == endDate)
            endDate.toEventDateTime()
        else
            endDate.plus(1.toDuration(DurationUnit.DAYS)).toEventDateTime()
        colorId = Color.Gray.id
        transparency = Availability.Free.value
    }

    private fun Instant.toEventDateTime() = EventDateTime().apply {
        date = DateTime(formatter.format(this@toEventDateTime.toJavaInstant()))
    }
}
