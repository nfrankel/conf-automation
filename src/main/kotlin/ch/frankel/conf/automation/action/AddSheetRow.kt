package ch.frankel.conf.automation.action

import ch.frankel.conf.automation.AppProperties
import ch.frankel.conf.automation.Status
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.bodyToMono

class AddSheetRow(private val props: AppProperties) : JavaDelegate {

    override fun execute(execution: DelegateExecution) {
        props.bearerToken()?.let {
            feishuClient
                .post()
                .uri("/sheets/v2/spreadsheets/${props.feishu.sheetId}/values_append")
                .headers { headers -> headers.setBearerAuth(it) }
                .body(
                    BodyInserters.fromValue(execution.conference.valuesAppendPayload)
                ).retrieve()
                .bodyToMono<String>()
                .block()
        }
    }

    private val Conference.valuesAppendPayload: ValuesAppendPayload
        get() = ValuesAppendPayload(
            ValueRange(
                props.feishu.tabId,
                arrayOf(
                    arrayOf(
                        Hyperlink(name, website),
                        props.speaker,
                        "",
                        location,
                        startDate.formatted,
                        endDate.formatted,
                        "Yes",
                        Status.Submitted.toString()
                    )
                )
            )
        )

    private data class ValuesAppendPayload(
        val valueRange: ValueRange
    )

    private data class ValueRange(
        val range: String,
        val values: Array<Array<Any>>
    )

    private data class Hyperlink(
        val text: String,
        val link: String,
        val type: String = "url"
    )
}

