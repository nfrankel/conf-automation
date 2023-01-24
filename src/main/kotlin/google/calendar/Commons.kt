package ch.frankel.conf.automation.google.calendar

import ch.frankel.conf.automation.AppProperties
import ch.frankel.conf.automation.GoogleProperties
import ch.frankel.conf.automation.action.Conference
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials
import java.time.LocalDate
import java.time.ZoneId

internal val TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()
internal val JSON_FACTORY = GsonFactory.getDefaultInstance()

internal val AppProperties.calendarCredentials: GoogleCredentials
    get() = GoogleCredentials.fromStream(google.calendar.json.byteInputStream())
        .createScoped(CalendarScopes.CALENDAR)

internal val AppProperties.calendarClient: Calendar
    get() = Calendar
        .Builder(TRANSPORT, JSON_FACTORY, HttpCredentialsAdapter(calendarCredentials))
        .setApplicationName(name)
        .build()

internal fun findCalendarEntry(
    client: Calendar,
    google: GoogleProperties,
    conference: Conference
) = client.events()
    .list(google.calendar.id)
    .setTimeMin(conference.startDate.minusDays(1).toDateTime())
    .setTimeMax(conference.endDate.plusDays(1).toDateTime())
    .execute()
    .items
    .find {
        it.summary == "${conference.name} (${conference.location})"
                && it.creator.email == google.calendar.clientEmail
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
