package ch.frankel.conf.automation

import ch.frankel.conf.automation.action.*
import ch.frankel.conf.automation.google.calendar.AddCalendarEntry
import ch.frankel.conf.automation.google.calendar.CalendarFactory
import ch.frankel.conf.automation.google.calendar.RemoveCalendarEntry
import ch.frankel.conf.automation.google.calendar.UpdateCalendarEntry
import ch.frankel.conf.automation.google.sheets.GoogleAddSheetRow
import ch.frankel.conf.automation.google.sheets.GoogleUpdateSheetRow
import ch.frankel.conf.automation.google.sheets.SheetsClientFactory
import ch.frankel.conf.automation.trello.ConfAutomationWebClientCustomizer
import ch.frankel.conf.automation.trello.TrelloRemoveDueDate
import ch.frankel.conf.automation.web.RegisterHandler
import ch.frankel.conf.automation.web.TriggerHandler
import ch.frankel.conf.automation.web.WhitelistIPFilterFunction
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

private fun beans() = beans {
    bean { ConfAutomationWebClientCustomizer(ref()) }
    bean { routes(ref(), ref(), ref(), ref()) }
    bean { CustomFieldsInitializer(ref(), ref()) }
    bean { ref<WebClient.Builder>().build() }
    bean { SheetsClientFactory(ref()).createInstance() }
    bean { CalendarFactory(ref()).createInstance() }
    bean("computeEventTransition") { ComputeEventTransition() }
    bean("removeDueDate") { RemoveDueDate(TrelloRemoveDueDate(ref())) }
    bean("extractConference") { ExtractConference(ref(), ref()) }
    bean("addCalendarEntry") { AddCalendarEntry(ref(), ref<AppProperties>().google.calendar) }
    bean("addSheetRow") {
        val props = ref<AppProperties>()
        val googleAddSheetRow = GoogleAddSheetRow(ref(), props.google.sheets, props.speaker)
        AddSheetRow(googleAddSheetRow)
    }
    bean("removeCalendarEntry") { RemoveCalendarEntry(ref(), ref<AppProperties>().google.calendar) }
    bean("updateSheetRow") {
        val props = ref<AppProperties>()
        val googleUpdateSheetRow = GoogleUpdateSheetRow(ref(), props.google.sheets, props.speaker)
        UpdateSheetRow(googleUpdateSheetRow)
    }
    bean("updateCalendarEntry") { UpdateCalendarEntry(ref(),  ref<AppProperties>().google.calendar) }
    profile("production") {
        bean { WhitelistIPFilterFunction(ref()) }
    }
}

private fun routes(
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
