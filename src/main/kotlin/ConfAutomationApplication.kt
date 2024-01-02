package ch.frankel.conf.automation

import ch.frankel.conf.automation.action.DeleteSheetRow
import ch.frankel.conf.automation.delegate.calendar.AddCalendarEntryDelegate
import ch.frankel.conf.automation.delegate.calendar.CalendarFactory
import ch.frankel.conf.automation.delegate.calendar.RemoveCalendarEntryDelegate
import ch.frankel.conf.automation.delegate.calendar.UpdateCalendarEntryDelegate
import ch.frankel.conf.automation.delegate.sheets.AddSheetRowDelegate
import ch.frankel.conf.automation.delegate.sheets.DeleteSheetRowDelegate
import ch.frankel.conf.automation.delegate.sheets.SheetsClientFactory
import ch.frankel.conf.automation.delegate.sheets.UpdateSheetRowDelegate
import ch.frankel.conf.automation.facade.*
import ch.frankel.conf.automation.delegate.trello.ConfAutomationRestClientCustomizer
import ch.frankel.conf.automation.delegate.trello.TickDueDateDelegate
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
import org.springframework.web.client.RestClient
import org.springframework.web.servlet.function.router

@SpringBootApplication
@EnableProcessApplication
@ConfigurationPropertiesScan
class ConfAutomationApplication

@OptIn(ExperimentalSerializationApi::class)
private fun beans() = beans {
    bean { ConfAutomationRestClientCustomizer(ref()) }
    bean { routes(ref(), ref(), ref(), ref(), ref()) }
    bean { CustomFieldsInitializer(ref(), ref()) }
    bean { ref<RestClient.Builder>().build() }
    bean { SheetsClientFactory(ref()).createInstance() }
    bean { CalendarFactory(ref()).createInstance() }
    bean("tickDueDate") { TickDueDate(TickDueDateDelegate(ref()), true) }
    bean("untickDueDate") { TickDueDate(TickDueDateDelegate(ref()), false) }
    bean("addCalendarEntry") { AddCalendarEntry(AddCalendarEntryDelegate(ref(), ref<AppProperties>().google.calendar)) }
    bean("addSheetRow") {
        val props = ref<AppProperties>()
        val addSheetRowDelegate = AddSheetRowDelegate(ref(), props.google.sheets, props.speaker)
        AddSheetRow(addSheetRowDelegate)
    }
    bean("removeCalendarEntry") { DeleteCalendarEntry(RemoveCalendarEntryDelegate(ref(), ref<AppProperties>().google.calendar)) }
    bean("acceptSheetRow") {
        val props = ref<AppProperties>()
        val updateSheetRowDelegate = UpdateSheetRowDelegate(ref(), props.google.sheets, props.speaker)
        UpdateSheetRow(updateSheetRowDelegate, Message.Accepted)
    }
    bean("refuseSheetRow") {
        val props = ref<AppProperties>()
        val updateSheetRowDelegate = UpdateSheetRowDelegate(ref(), props.google.sheets, props.speaker)
        UpdateSheetRow(updateSheetRowDelegate, Message.Refused)
    }
    bean("abandonSheetRow") {
        val props = ref<AppProperties>()
        val updateSheetRowDelegate = UpdateSheetRowDelegate(ref(), props.google.sheets, props.speaker)
        UpdateSheetRow(updateSheetRowDelegate, Message.Abandoned)
    }
    bean("deleteSheetRow") {
        val props = ref<AppProperties>()
        val deleteSheetRowDelegate = DeleteSheetRowDelegate(ref(), props.google.sheets, props.speaker)
        DeleteSheetRow(deleteSheetRowDelegate)
    }
    bean("updateCalendarEntry") { UpdateCalendarEntry(UpdateCalendarEntryDelegate(ref(),  ref<AppProperties>().google.calendar)) }
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
    client: RestClient
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
