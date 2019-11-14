package ch.frankel.conf.automation

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@ConfigurationProperties("application")
@Component
class AppProperties {
    lateinit var speaker: String
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
    lateinit var sheetId: String
    lateinit var calendarId: String
    lateinit var privateKeyId: String
    lateinit var privateKey: String
    lateinit var clientEmail: String
}