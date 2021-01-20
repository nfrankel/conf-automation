package ch.frankel.conf.automation

import org.springframework.boot.web.reactive.function.client.WebClientCustomizer

internal fun webClientCustomizer(props: AppProperties) = WebClientCustomizer {
    it.baseUrl("https://api.trello.com/1")
    it.defaultUriVariables(
        mapOf(
            "key" to props.trello.key,
            "token" to props.trello.token
        )
    )
}