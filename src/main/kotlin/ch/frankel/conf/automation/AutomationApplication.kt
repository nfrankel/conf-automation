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
class AutomationApplication

fun beans() = beans {
    bean {
        routes(ref())
    }
    bean { CustomFieldsInitializer(ref()) }
    bean("computeEventType") { computeEventType }
    bean("removeDueDate") { RemoveDueDate(ref()) }
    bean("addCalendarEntry") { AddCalendarEntry(ref(), ref()) }
    bean("addSheetRow") { AddSheetRow(ref(), ref()) }
}

fun routes(runtimeService: RuntimeService) =
    router {
        val handler = TriggerHandler(runtimeService)
        POST("/trigger", handler::post)
        HEAD("/trigger", handler::head)
    }

fun main(args: Array<String>) {
    runApplication<AutomationApplication>(*args) {
        addInitializers(beans())
    }
}