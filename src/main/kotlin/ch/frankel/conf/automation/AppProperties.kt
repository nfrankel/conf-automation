package ch.frankel.conf.automation

import camundajar.impl.com.google.gson.Gson
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("application")
class AppProperties {
    lateinit var speaker: String
    lateinit var name: String
    val trello = TrelloProperties()
    val google = GoogleProperties()
}

class TrelloProperties {
    lateinit var key: String
    lateinit var token: String
    lateinit var boardShortLink: String
    lateinit var boardId: String
    val ips = mutableSetOf<String>()
}

class GoogleProperties {
    lateinit var json: String
    lateinit var sheetId: String
    lateinit var calendarId: String
    val clientEmail: String by lazy {
        Gson().fromJson(json, Map::class.java)["client_email"] as String
    }
}