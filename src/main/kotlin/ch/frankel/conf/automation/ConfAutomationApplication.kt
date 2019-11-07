package ch.frankel.conf.automation

import ch.frankel.conf.automation.action.*
import org.camunda.bpm.engine.RuntimeService
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.support.beans
import org.springframework.web.servlet.function.router

@SpringBootApplication
@EnableProcessApplication
class ConfAutomationApplication

fun beans() = beans {
    bean { routes(ref()) }
    bean { WhitelistIPFilterRegistrationBean(ref()) }
    bean { CustomFieldsInitializer(ref()) }
    bean("computeEventStatus") { computeEventStatus }
    bean("removeDueDate") { RemoveDueDate(ref()) }
    bean("extractConference") { ExtractConference(ref(), ref()) }
    bean("addCalendarEntry") { AddCalendarEntry(ref()) }
    bean("addSheetRow") { AddSheetRow(ref()) }
    bean("removeCalendarEntry") { RemoveCalendarEntry(ref()) }
    bean("updateSheetRow") { UpdateSheetRow(ref()) }
}

fun routes(runtimeService: RuntimeService) =
    router {
        val handler = TriggerHandler(runtimeService)
        POST("/trigger", handler::post)
        HEAD("/trigger", handler::head)
    }

fun main(args: Array<String>) {
    runApplication<ConfAutomationApplication>(*args) {
        addInitializers(beans())
    }
}