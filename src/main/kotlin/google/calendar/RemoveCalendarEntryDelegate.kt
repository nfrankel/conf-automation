package ch.frankel.conf.automation.google.calendar

import ch.frankel.conf.automation.CalendarProperties
import ch.frankel.conf.automation.action.Conference
import com.google.api.services.calendar.Calendar
import org.slf4j.LoggerFactory

class RemoveCalendarEntryDelegate(private val client: Calendar, private val props: CalendarProperties) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun execute(conference: Conference) {
        logger.info("Trying to remove calendar entry for ${conference.name}")
        findCalendarEntry(client, props, conference)?.let {
            client.events().delete(props.id, it.id).execute()
            logger.info("Calendar entry removed for ${conference.name}")
        }
    }
}