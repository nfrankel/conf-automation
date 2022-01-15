package ch.frankel.conf.automation

import ch.frankel.conf.automation.action.*
import org.camunda.bpm.engine.RuntimeService
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.context.support.beans
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.servlet.function.router

@SpringBootApplication
@EnableProcessApplication
@ConfigurationPropertiesScan
class ConfAutomationApplication

fun beans() = beans {
    bean { webClientCustomizer(ref()) }
    bean { routes(ref(), ref(), ref(), ref()) }
    bean { CustomFieldsInitializer(ref(), ref()) }
    bean { ref<WebClient.Builder>().build() }
    bean("computeEventStatus") { computeEventStatus }
    bean("removeDueDate") { RemoveDueDate(ref()) }
    bean("extractConference") { ExtractConference(ref(), ref()) }
    bean("addCalendarEntry") { AddCalendarEntry(ref()) }
    bean("addSheetRow") { NoOp }
    bean("removeCalendarEntry") { RemoveCalendarEntry(ref()) }
    bean("updateSheetRow") { NoOp }
    bean("updateCalendarEntry") { UpdateCalendarEntry(ref()) }
    profile("production") {
        bean { WhitelistIPFilterFunction(ref()) }
    }
}

fun routes(
    runtimeService: RuntimeService,
    props: AppProperties,
    whiteListIP: WhitelistIPFilterFunction,
    client: WebClient
) = router {
    val trigger = TriggerHandler(runtimeService)
    POST("/trigger", trigger::post)
    HEAD("/trigger", trigger::head)
}.and(
    router {
        val register = RegisterHandler(props, client)
        POST("/register", register::post)
    }.filter(whiteListIP)
)

fun main(args: Array<String>) {
    runApplication<ConfAutomationApplication>(*args) {
        addInitializers(beans())
    }
}