package ch.frankel.conf.automation.action

import ch.frankel.conf.automation.AppProperties
import org.springframework.http.*
import org.springframework.web.client.RestTemplate

class CustomFieldsInitializer(props: AppProperties, template: RestTemplate) {

    val fields = template.exchange(
        "/boards/{id}/customFields?key={key}&token={token}",
        HttpMethod.GET,
        HttpEntity.EMPTY,
        typeRef<List<CustomField>>(),
        mapOf("id" to props.trello.boardShortLink)
    ).body.orEmpty()
        .map { it.id to it.name }
        .toMap()

    private data class CustomField(
        var id: String,
        var name: String
    )
}