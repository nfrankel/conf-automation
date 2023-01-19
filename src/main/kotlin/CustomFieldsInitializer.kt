package ch.frankel.conf.automation

import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux

class CustomFieldsInitializer(props: AppProperties, builder: WebClient.Builder) {

    val fields: Flux<Pair<String, String>> = builder.build().get()
        .uri("/boards/${props.trello.boardShortLink}/customFields?key={key}&token={token}")
        .retrieve()
        .bodyToFlux(CustomField::class.java)
        .map { it.id to it.name }

    private data class CustomField(
        var id: String,
        var name: String
    )
}
