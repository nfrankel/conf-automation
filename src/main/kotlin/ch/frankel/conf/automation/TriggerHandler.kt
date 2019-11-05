package ch.frankel.conf.automation

import ch.frankel.conf.automation.trigger.TrelloEvent
import org.camunda.bpm.engine.RuntimeService
import org.springframework.web.servlet.function.*


class TriggerHandler(private val runtimeService: RuntimeService) {
    fun post(request: ServerRequest): ServerResponse {
        val event = request.body<TrelloEvent>()
        val params = mapOf("event" to event)
        val id = runtimeService
            .startProcessInstanceByKey("HandleChange", params)
            .processDefinitionId
        return ServerResponse
            .accepted()
            .body("{ id: $id }")
    }

    /* Required by Trello, as it executes a HEAD request to make sure the endpoint is up. */
    fun head(request: ServerRequest) = ServerResponse.ok().build()
}

