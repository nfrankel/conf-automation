package ch.frankel.conf.automation

import org.camunda.bpm.engine.RuntimeService
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono


class TriggerHandler(private val runtimeService: RuntimeService) {

    companion object {
        const val BPMN_EVENT = "event"
    }

    fun post(request: ServerRequest): Mono<ServerResponse> {
        val event = request.bodyToMono(TrelloEvent::class.java).block()
        val params = mapOf(BPMN_EVENT to event)
        val id = runtimeService
            .startProcessInstanceByKey("HandleChange", params)
            .processDefinitionId
        return ServerResponse
            .accepted()
            .bodyValue("{ id: $id }")
    }

    /* Required by Trello, as it executes a HEAD request to make sure the endpoint is up. */
    fun head(@Suppress("UNUSED_PARAMETER") request: ServerRequest) = ServerResponse.ok().build()
}

