package ch.frankel.conf.automation.delegate.calendar

import ch.frankel.conf.automation.CalendarProperties
import ch.frankel.conf.automation.action.Conference
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.Calendar
import kotlinx.datetime.*
import kotlin.time.DurationUnit
import kotlin.time.toDuration

internal fun findCalendarEntry(
    client: Calendar,
    calendar: CalendarProperties,
    conference: Conference
) = client.events()
    .list(calendar.id)
    .setTimeMin(conference.startDate.minus(1.toDuration(DurationUnit.DAYS)).toDateTime())
    .setTimeMax(conference.endDate.plus(1.toDuration(DurationUnit.DAYS)).toDateTime())
    .execute()
    .items
    .find {
        it.summary == "${conference.name} (${conference.location})"
                && it.creator.email == calendar.clientEmail
    }

private fun Instant.toDateTime() = DateTime(toEpochMilliseconds())

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
