package ch.frankel.conf.automation

import ch.frankel.conf.automation.action.*
import org.camunda.bpm.engine.RuntimeService
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.support.beans
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.server.router

@SpringBootApplication
@EnableProcessApplication
class ConfAutomationApplication

fun beans() = beans {
    bean { webClientCustomizer(ref()) }
    bean { routes(ref(), ref(), ref(), ref()) }
    bean { CustomFieldsInitializer(ref(), ref()) }
    bean("computeEventStatus") { computeEventStatus }
    bean("removeDueDate") { RemoveDueDate(ref()) }
    bean("extractConference") { ExtractConference(ref(), ref()) }
    bean("addCalendarEntry") { AddCalendarEntry(ref()) }
    bean("addSheetRow") { AddSheetRow(ref()) }
    bean("removeCalendarEntry") { RemoveCalendarEntry(ref()) }
    bean("updateSheetRow") { UpdateSheetRow(ref()) }
    bean("updateCalendarEntry") { UpdateCalendarEntry(ref()) }
    profile("production") {
        bean { WhitelistIPFilterFunction(ref()) }
    }
}

fun routes(
    runtimeService: RuntimeService,
    props: AppProperties,
    whiteListIP: WhitelistIPFilterFunction,
    builder: WebClient.Builder
) = router {
    val trigger = TriggerHandler(runtimeService)
    POST("/trigger", trigger::post)
    HEAD("/trigger", trigger::head)
}.and(
    router {
        val register = RegisterHandler(props, builder)
        POST("/register", register::post)
    }.filter(whiteListIP)
)

fun main(args: Array<String>) {
    runApplication<ConfAutomationApplication>(*args) {
        addInitializers(beans())
    }
}