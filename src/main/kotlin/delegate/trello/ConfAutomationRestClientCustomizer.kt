package ch.frankel.conf.automation.delegate.trello

import ch.frankel.conf.automation.AppProperties
import org.springframework.boot.web.client.RestClientCustomizer
import org.springframework.web.client.RestClient

class ConfAutomationRestClientCustomizer(private val props: AppProperties) : RestClientCustomizer {

    override fun customize(builder: RestClient.Builder) {
        builder.baseUrl("https://api.trello.com/1")
        builder.defaultUriVariables(
            mapOf(
                "key" to props.trello.key,
                "token" to props.trello.token
            )
        )
    }
}
