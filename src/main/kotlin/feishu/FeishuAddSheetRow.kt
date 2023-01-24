package ch.frankel.conf.automation.feishu

import ch.frankel.conf.automation.AppProperties
import ch.frankel.conf.automation.action.Conference
import ch.frankel.conf.automation.action.Status
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.bodyToMono
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class FeishuAddSheetRow(private val props: AppProperties, private val client: FeishuClient) {

    fun execute(conference: Conference) {
        client.getBearerToken()
            .flatMap { bearer ->
                client.post()
                    .uri("/sheets/v2/spreadsheets/${props.feishu.sheetId}/values_append?insertDataOption=INSERT_ROWS")
                    .headers { it.setBearerAuth(bearer.token) }
                    .body(
                        BodyInserters.fromValue(conference.toPayload(props.feishu.tabId, props.speaker))
                    ).retrieve()
                    .bodyToMono<String>()
            }.block()
    }
}

private fun Conference.toPayload(tabId: String, speaker: String) = ValuesAppendPayload(
    ValueRange(
        "$tabId!A3:H3",
        arrayOf(
            arrayOf(
                Hyperlink(name, website),
                speaker,
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

private data class ValuesAppendPayload(val valueRange: ValueRange)

private data class ValueRange(val range: String, val values: Array<Array<Any>>)

private data class Hyperlink(
    val text: String,
    val link: String,
    val type: String = "url"
)

private val LocalDate.formatted: String
    get() = DateTimeFormatter.ISO_LOCAL_DATE.format(this)

