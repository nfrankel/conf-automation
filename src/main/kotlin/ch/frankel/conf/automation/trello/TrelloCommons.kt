package ch.frankel.conf.automation.trello

import ch.frankel.conf.automation.AppProperties
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer
import org.springframework.web.reactive.function.client.WebClient

class ConfAutomationWebClientCustomizer(private val props: AppProperties) : WebClientCustomizer {

    override fun customize(builder: WebClient.Builder) {
        builder.baseUrl("https://api.trello.com/1")
        builder.defaultUriVariables(
            mapOf(
                "key" to props.trello.key,
                "token" to props.trello.token
            )
        )
    }
}
