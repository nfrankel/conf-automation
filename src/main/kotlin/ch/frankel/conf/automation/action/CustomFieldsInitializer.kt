package ch.frankel.conf.automation.action

import ch.frankel.conf.automation.AppProperties
import org.springframework.http.*
import org.springframework.web.client.RestTemplate

/**
 By GETting a Trello card, one gets custom field values, and their id, but not their name <i>e.g.</i> Start date.
 This class stores a map of custom field ids to custom field names.
 */
class CustomFieldsInitializer(props: AppProperties) {

    val fields: Map<String, String>?

    init {
        val params = mapOf("id" to props.trello.boardShortLink,
            "key" to props.trello.key,
            "token" to props.trello.token)
        val response = RestTemplate().exchange(
            "https://api.trello.com/1/boards/{id}/customFields?key={key}&token={token}",
            HttpMethod.GET,
            HttpEntity<Any>(HttpHeaders()),
            typeRef<List<CustomField>>(),
            params
        )
        fields = response.body?.map {
            it.id to it.name
        }?.toMap()
    }

    private data class CustomField(
        var id: String,
        var name: String
    )
}