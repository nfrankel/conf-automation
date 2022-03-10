package ch.frankel.conf.automation.action

import ch.frankel.conf.automation.AppProperties
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
                    BodyInserters.fromValue(execution.conference.toFeishuSheetData)
                ).retrieve()
                .bodyToMono<String>()
                .block()
        }
    }

    private val Conference.toFeishuSheetData: String
        get() = """
        {
            "valueRange": {
                "range": "${props.feishu.tabId}",
                "values": [[
                    {
                        "text": "$name",
                        "link": "$website",
                        "type": "url"
                    },
                    "${props.speaker}",
                    "",
                    "$location",
                    "${startDate.formatted}",
                    "${endDate.formatted}",
                    "Yes",
                    "Submitted"
                ]]
            }
        }
        """.trimIndent()
}

