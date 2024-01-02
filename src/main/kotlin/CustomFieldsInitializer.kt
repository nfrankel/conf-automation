package ch.frankel.conf.automation

import org.springframework.core.ParameterizedTypeReference
import org.springframework.web.client.RestClient
import kotlin.collections.List

class CustomFieldsInitializer(props: AppProperties, builder: RestClient.Builder) {

    val fields: List<Pair<String, String>> = builder.build().get()
        .uri("/boards/${props.trello.boardShortLink}/customFields?key={key}&token={token}")
        .retrieve()
        .body(object: ParameterizedTypeReference<List<CustomField>>() {})
        ?.map { it.id to it.name } ?: listOf()

    private data class CustomField(
        var id: String,
        var name: String
    )
}
