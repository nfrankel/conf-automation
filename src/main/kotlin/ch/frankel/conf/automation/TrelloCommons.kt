package ch.frankel.conf.automation

import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.boot.web.client.RestTemplateCustomizer
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.DefaultUriBuilderFactory

internal fun trelloClient(props: AppProperties): RestTemplate {
    val customizer = RestTemplateCustomizer {
        val params = mapOf(
            "key" to props.trello.key,
            "token" to props.trello.token
        )
        it.uriTemplateHandler = DefaultUriBuilderFactory("https://api.trello.com/1")
            .apply { setDefaultUriVariables(params) }
    }
    return RestTemplateBuilder(customizer).build()
}