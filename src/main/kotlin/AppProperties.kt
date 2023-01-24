package ch.frankel.conf.automation

import camundajar.impl.com.google.gson.Gson
import org.springframework.boot.context.properties.*

@ConfigurationProperties("application")
@ConstructorBinding
data class AppProperties(
    val speaker: String,
    val name: String,
    val trello: TrelloProperties,
    val google: GoogleProperties,
    val feishu: FeishuProperties
)

data class TrelloProperties(
    val key: String,
    val token: String,
    val boardShortLink: String,
    val boardId: String,
    val ips: Set<String>
)

data class FeishuProperties(
    val sheetId: String,
    val tabId: String,
    val appId: String,
    val appSecret: String,
    val maxRow: Int
)

data class GoogleProperties(
    val calendar: Calendar,
    val calendarId: String
)

data class Calendar(
    val json: String,
) {
    val clientEmail: String by lazy {
        Gson().fromJson(json, Map::class.java)["client_email"] as String
    }
}
