package ch.frankel.conf.automation.delegate.calendar

import ch.frankel.conf.automation.CalendarProperties
import ch.frankel.conf.automation.action.Conference
import com.google.api.services.calendar.Calendar

class UpdateCalendarEntryDelegate(private val client: Calendar, private val props: CalendarProperties) {

    fun execute(conference: Conference) {
        findCalendarEntry(client, props, conference)?.let {
            it.colorId = Color.Default.id
            it.transparency = Availability.Busy.value
            client.events().update(props.id, it.id, it).execute()
        }
    }
}