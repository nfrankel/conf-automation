package ch.frankel.conf.automation

import ch.frankel.conf.automation.action.*
import ch.frankel.conf.automation.feishu.FeishuAddSheetRow
import ch.frankel.conf.automation.feishu.FeishuClient
import ch.frankel.conf.automation.feishu.FeishuUpdateSheetRow
import ch.frankel.conf.automation.google.calendar.AddCalendarEntry
import ch.frankel.conf.automation.google.calendar.CalendarFactory
import ch.frankel.conf.automation.google.calendar.RemoveCalendarEntry
import ch.frankel.conf.automation.google.calendar.UpdateCalendarEntry
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

fun beans() = beans {
    bean { ConfAutomationWebClientCustomizer(ref()) }
    bean { routes(ref(), ref(), ref(), ref()) }
    bean { CustomFieldsInitializer(ref(), ref()) }
    bean { ref<WebClient.Builder>().build() }
    bean { FeishuClient(ref()) }
    bean { CalendarFactory(ref()).createInstance() }
    bean("computeEventStatus") { ComputeEventStatus() }
    bean("removeDueDate") { RemoveDueDate(TrelloRemoveDueDate(ref())) }
    bean("extractConference") { ExtractConference(ref(), ref()) }
    bean("addCalendarEntry") { AddCalendarEntry(ref(), ref<AppProperties>().google.calendar) }
    bean("addSheetRow") { AddSheetRow(FeishuAddSheetRow(ref(), ref())) }
    bean("removeCalendarEntry") { RemoveCalendarEntry(ref(), ref<AppProperties>().google.calendar) }
    bean("updateSheetRow") { UpdateSheetRow(FeishuUpdateSheetRow(ref(), ref())) }
    bean("updateCalendarEntry") { UpdateCalendarEntry(ref(),  ref<AppProperties>().google.calendar) }
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
