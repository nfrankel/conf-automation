package ch.frankel.conf.automation

import ch.frankel.conf.automation.action.*
import ch.frankel.conf.automation.google.calendar.AddCalendarEntry
import ch.frankel.conf.automation.google.calendar.CalendarFactory
import ch.frankel.conf.automation.google.calendar.RemoveCalendarEntry
import ch.frankel.conf.automation.google.calendar.UpdateCalendarEntry
import ch.frankel.conf.automation.google.sheets.GoogleAddSheetRow
import ch.frankel.conf.automation.google.sheets.GoogleDeleteSheetRow
import ch.frankel.conf.automation.google.sheets.GoogleUpdateSheetRow
import ch.frankel.conf.automation.google.sheets.SheetsClientFactory
import ch.frankel.conf.automation.trello.ConfAutomationWebClientCustomizer
import ch.frankel.conf.automation.trello.TrelloTickDueDate
import ch.frankel.conf.automation.web.RegisterHandler
import ch.frankel.conf.automation.web.TriggerHandler
import ch.frankel.conf.automation.web.WhitelistIPFilterFunction
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.camunda.bpm.engine.RuntimeService
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.context.support.beans
import org.springframework.http.converter.json.KotlinSerializationJsonHttpMessageConverter
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.servlet.function.router

@SpringBootApplication
@EnableProcessApplication
@ConfigurationPropertiesScan
class ConfAutomationApplication

@OptIn(ExperimentalSerializationApi::class)
private fun beans() = beans {
    bean { ConfAutomationWebClientCustomizer(ref()) }
    bean { routes(ref(), ref(), ref(), ref(), ref()) }
    bean { CustomFieldsInitializer(ref(), ref()) }
    bean { ref<WebClient.Builder>().build() }
    bean { SheetsClientFactory(ref()).createInstance() }
    bean { CalendarFactory(ref()).createInstance() }
    bean("tickDueDate") { TickDueDate(TrelloTickDueDate(ref()), true) }
    bean("untickDueDate") { TickDueDate(TrelloTickDueDate(ref()), false) }
    bean("addCalendarEntry") { AddCalendarEntry(ref(), ref<AppProperties>().google.calendar) }
    bean("addSheetRow") {
        val props = ref<AppProperties>()
        val googleAddSheetRow = GoogleAddSheetRow(ref(), props.google.sheets, props.speaker)
        AddSheetRow(googleAddSheetRow)
    }
    bean("removeCalendarEntry") { RemoveCalendarEntry(ref(), ref<AppProperties>().google.calendar) }
    bean("acceptSheetRow") {
        val props = ref<AppProperties>()
        val googleUpdateSheetRow = GoogleUpdateSheetRow(ref(), props.google.sheets, props.speaker)
        UpdateSheetRow(googleUpdateSheetRow, Message.Accepted)
    }
    bean("refuseSheetRow") {
        val props = ref<AppProperties>()
        val googleUpdateSheetRow = GoogleUpdateSheetRow(ref(), props.google.sheets, props.speaker)
        UpdateSheetRow(googleUpdateSheetRow, Message.Refused)
    }
    bean("abandonSheetRow") {
        val props = ref<AppProperties>()
        val googleUpdateSheetRow = GoogleUpdateSheetRow(ref(), props.google.sheets, props.speaker)
        UpdateSheetRow(googleUpdateSheetRow, Message.Abandoned)
    }
    bean("deleteSheetRow") {
        val props = ref<AppProperties>()
        val googleDeleteSheetRow = GoogleDeleteSheetRow(ref(), props.google.sheets, props.speaker)
        DeleteSheetRow(googleDeleteSheetRow)
    }
    bean("updateCalendarEntry") { UpdateCalendarEntry(ref(),  ref<AppProperties>().google.calendar) }
    bean { LogAppVersionStartupListener(ref<AppProperties>().version) }
    bean { KotlinSerializationJsonHttpMessageConverter(
        Json {
            ignoreUnknownKeys = true
            explicitNulls = false
        }
    )}
    profile("production") {
        bean { WhitelistIPFilterFunction(ref()) }
    }
}

private fun routes(
    runtimeService: RuntimeService,
    customFieldsInitializer: CustomFieldsInitializer,
    props: AppProperties,
    whiteListIP: WhitelistIPFilterFunction,
    client: WebClient
) = router {
    val trigger = TriggerHandler(runtimeService, customFieldsInitializer, client, mutableSetOf())
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
