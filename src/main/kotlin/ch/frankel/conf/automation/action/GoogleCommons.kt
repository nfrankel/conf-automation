package ch.frankel.conf.automation.action

import ch.frankel.conf.automation.AppProperties
import ch.frankel.conf.automation.GoogleProperties
import ch.frankel.conf.automation.action.ExtractConference.Companion.BPMN_CONFERENCE
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.util.*
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.sheets.v4.SheetsScopes
import org.camunda.bpm.engine.delegate.DelegateExecution
import java.io.StringReader
import java.security.PrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

internal val DelegateExecution.conference: Conference
    get() = getVariable(BPMN_CONFERENCE) as Conference

internal val LocalDate.formatted: String
    get() = DateTimeFormatter.ISO_LOCAL_DATE.format(this)

internal val AppProperties.credential: GoogleCredential
    get() = GoogleCredential.Builder().apply {
        transport = TRANSPORT
        jsonFactory = JSON_FACTORY
        serviceAccountId = google.clientEmail
        serviceAccountPrivateKeyId = google.privateKeyId
        serviceAccountPrivateKey = google.privateKey.toPrivateKey()
        serviceAccountScopes = listOf(CalendarScopes.CALENDAR, SheetsScopes.SPREADSHEETS)
    }.build()

internal fun findCalendarEntry(client: Calendar,
                               google: GoogleProperties,
                               conference: Conference) =
    client.events()
        .list(google.calendarId)
        .setTimeMin(conference.startDate.minusDays(1).toDateTime())
        .setTimeMax(conference.endDate.plusDays(1).toDateTime())
        .execute()
        .items
        .find {
            it.summary == "${conference.name} (${conference.location})"
            it.creator.email == google.clientEmail
        }

private fun LocalDate.toDateTime(): DateTime {
    val millis = atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    return DateTime(millis)
}

/* Inspired from com.google.api.client.googleapis.auth.oauth2.GoogleCredential.privateKeyFromPkcs8() .*/
private fun String.toPrivateKey(): PrivateKey {
    val pemReader = PemReader(StringReader(this))
    val section = pemReader.readNextSection("PRIVATE KEY")
    val keySpec = PKCS8EncodedKeySpec(section.base64DecodedBytes)
    val keyFactory = SecurityUtils.getRsaKeyFactory()
    return keyFactory.generatePrivate(keySpec)
}