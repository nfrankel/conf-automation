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
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.*
import org.springframework.web.client.RestTemplate
import java.io.StringReader
import java.security.PrivateKey
import java.security.spec.PKCS8EncodedKeySpec

abstract class GoogleAction(private val props: AppProperties,
                            private val fieldsInitializer: CustomFieldsInitializer) : JavaDelegate {

    internal val credential = props.toCredential()

    private val DelegateExecution.customFields: List<Field<out Any>>
        get() {
            val params = mapOf("id" to event.cardId,
                "key" to props.trello.key,
                "token" to props.trello.token)
            val response = RestTemplate().exchange(
                "https://api.trello.com/1/cards/{id}/customFieldItems?key={key}&token={token}",
                HttpMethod.GET,
                HttpEntity<Any>(HttpHeaders()),
                typeRef<List<CustomFieldItem>>(),
                params
            )
            return response.body.orEmpty().map {
                val fieldName = fieldsInitializer.fields?.get(it.idCustomField)
                when {
                    it.value?.text != null && fieldName == "Site" -> Website(it)
                    it.value?.date != null && fieldName == "Start date" -> StartDate(it)
                    it.value?.date != null && fieldName == "End date" -> EndDate(it)
                    else -> IrrelevantField
                }
            }.filter { it !is IrrelevantField }
        }

    internal fun extractConference(execution: DelegateExecution): Conference {
        val fields = execution.customFields
        val name = execution.event.action.data.card.name
        return Conference(name, fields)
    }
}

internal val TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()
internal val JSON_FACTORY = JacksonFactory.getDefaultInstance()

internal inline fun <reified T : Any> typeRef(): ParameterizedTypeReference<T> = object : ParameterizedTypeReference<T>() {}

internal val DelegateExecution.event: TrelloEvent
    get() = getVariable("event") as TrelloEvent

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