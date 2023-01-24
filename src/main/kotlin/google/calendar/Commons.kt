package ch.frankel.conf.automation.google.calendar

import ch.frankel.conf.automation.CalendarProperties
import ch.frankel.conf.automation.GoogleProperties
import ch.frankel.conf.automation.action.Conference
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.Calendar
import java.time.LocalDate
import java.time.ZoneId

internal fun findCalendarEntry(
    client: Calendar,
    calendar: CalendarProperties,
    conference: Conference
) = client.events()
    .list(calendar.id)
    .setTimeMin(conference.startDate.minusDays(1).toDateTime())
    .setTimeMax(conference.endDate.plusDays(1).toDateTime())
    .execute()
    .items
    .find {
        it.summary == "${conference.name} (${conference.location})"
                && it.creator.email == calendar.clientEmail
    }

private fun LocalDate.toDateTime(): DateTime {
    val millis = atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    return DateTime(millis)
}

@Suppress("unused")
internal enum class Color(val id: String) {
    Default("0"),
    Blue("1"),
    Green("2"),
    Purple("3"),
    Red("4"),
    Yellow("5"),
    Orange("6"),
    Turquoise("7"),
    Gray("8"),
    BoldBlue("9"),
    BoldGreen("10"),
    BoldRed("11")
}

internal enum class Availability(val value: String) {
    Free("transparent"),
    Busy("opaque"),
}
