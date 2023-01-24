package ch.frankel.conf.automation.web

import ch.frankel.conf.automation.TrelloEvent
import org.camunda.bpm.engine.RuntimeService
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse


class TriggerHandler(private val runtimeService: RuntimeService) {

    fun post(request: ServerRequest): ServerResponse {
        val event = request.body(TrelloEvent::class.java)
        val params = mapOf("event" to event)
        val id = runtimeService
            .startProcessInstanceByKey("HandleChange", params)
            .processDefinitionId
        return ServerResponse
            .accepted()
            .body("{ id: $id }")
    }

    /* Required by Trello, as it executes a HEAD request to make sure the endpoint is up. */
    fun head(@Suppress("UNUSED_PARAMETER") request: ServerRequest) = ServerResponse.ok().build()
}
