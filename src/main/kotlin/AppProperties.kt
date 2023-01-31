package ch.frankel.conf.automation

import camundajar.impl.com.google.gson.Gson
import org.springframework.boot.context.properties.*

@ConfigurationProperties("application")
@ConstructorBinding
data class AppProperties(
    val speaker: String,
    val name: String,
    val trello: TrelloProperties,
    val google: GoogleProperties
)

data class TrelloProperties(
    val key: String,
    val token: String,
    val boardShortLink: String,
    val boardId: String,
    val ips: Set<String>
)

data class GoogleProperties(
    val calendar: CalendarProperties,
    val sheets: SheetsProperties,
)

data class CalendarProperties(
    val id: String,
    val json: String,
) {
    val clientEmail: String by lazy {
        Gson().fromJson(json, Map::class.java)["client_email"] as String
    }
}

data class SheetsProperties(
    val id: String,
    val json: String
)
