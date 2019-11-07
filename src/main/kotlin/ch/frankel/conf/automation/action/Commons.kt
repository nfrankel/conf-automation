package ch.frankel.conf.automation.action

import ch.frankel.conf.automation.AppProperties
import ch.frankel.conf.automation.TrelloEvent
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.PemReader
import com.google.api.client.util.SecurityUtils
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.sheets.v4.SheetsScopes
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.springframework.core.ParameterizedTypeReference
import java.io.StringReader
import java.security.PrivateKey
import java.security.spec.PKCS8EncodedKeySpec

internal val TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()
internal val JSON_FACTORY = JacksonFactory.getDefaultInstance()

internal inline fun <reified T : Any> typeRef(): ParameterizedTypeReference<T> = object : ParameterizedTypeReference<T>() {}

internal val DelegateExecution.event: TrelloEvent
    get() = getVariable("event") as TrelloEvent

internal val DelegateExecution.conference: Conference
    get() = getVariable("conference") as Conference

internal val TrelloEvent.cardId: String
    get() = this.action.data.card.id

internal fun AppProperties.toCredential() = GoogleCredential.Builder().apply {
    transport = TRANSPORT
    jsonFactory = JSON_FACTORY
    serviceAccountId = this@toCredential.google.clientEmail
    serviceAccountPrivateKeyId = this@toCredential.google.privateKeyId
    serviceAccountPrivateKey = this@toCredential.google.privateKey.toPrivateKey()
    serviceAccountScopes = listOf(CalendarScopes.CALENDAR, SheetsScopes.SPREADSHEETS)
}.build()

/* Inspired from com.google.api.client.googleapis.auth.oauth2.GoogleCredential.privateKeyFromPkcs8() .*/
private fun String.toPrivateKey(): PrivateKey {
    val pemReader = PemReader(StringReader(this))
    val section = pemReader.readNextSection("PRIVATE KEY")
    val keySpec = PKCS8EncodedKeySpec(section.base64DecodedBytes)
    val keyFactory = SecurityUtils.getRsaKeyFactory()
    return keyFactory.generatePrivate(keySpec)
}