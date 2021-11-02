package ch.frankel.conf.automation.action

import ch.frankel.conf.automation.AppProperties
import ch.frankel.conf.automation.GoogleProperties
import ch.frankel.conf.automation.action.ExtractConference.Companion.BPMN_CONFERENCE
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials
import org.camunda.bpm.engine.delegate.DelegateExecution
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

internal val DelegateExecution.conference: Conference
    get() = getVariable(BPMN_CONFERENCE) as Conference

internal val LocalDate.formatted: String
    get() = DateTimeFormatter.ISO_LOCAL_DATE.format(this)

internal val AppProperties.credential: GoogleCredentials
    get() = GoogleCredentials.fromStream(google.json.byteInputStream())
        .createScoped(CalendarScopes.CALENDAR, SheetsScopes.SPREADSHEETS)

internal val AppProperties.sheetsClient: Sheets
    get() = Sheets
        .Builder(TRANSPORT, JSON_FACTORY, HttpCredentialsAdapter(credential))
        .setApplicationName(name)
        .build()

internal val AppProperties.calendarClient: Calendar
    get() = Calendar
        .Builder(TRANSPORT, JSON_FACTORY, HttpCredentialsAdapter(credential))
        .setApplicationName(name)
        .build()

internal fun findCalendarEntry(
    client: Calendar,
    google: GoogleProperties,
    conference: Conference
) =
    client.events()
        .list(google.calendarId)
        .setTimeMin(conference.startDate.minusDays(1).toDateTime())
        .setTimeMax(conference.endDate.plusDays(1).toDateTime())
        .execute()
        .items
        .find {
            it.summary == "${conference.name} (${conference.location})"
                && it.creator.email == google.clientEmail
        }

private fun LocalDate.toDateTime(): DateTime {
    val millis = atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    return DateTime(millis)
}

@Suppress("unused")
internal enum class Color(val id: String) {
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
