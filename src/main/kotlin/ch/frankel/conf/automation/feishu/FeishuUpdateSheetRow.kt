package ch.frankel.conf.automation.feishu

import ch.frankel.conf.automation.AppProperties
import ch.frankel.conf.automation.Status
import ch.frankel.conf.automation.action.Conference
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import reactor.util.Loggers

class FeishuUpdateSheetRow(private val props: AppProperties, private val client: FeishuClient) {

    fun execute(conference: Conference, status: Status) {
        client.getBearerToken()
            .flatMap { bearer ->
                val search = SearchPayload(conference.name, "${props.feishu.tabId}!A3:A${props.feishu.maxRow}")
                client.post()
                    .uri("/sheets/v3/spreadsheets/${props.feishu.sheetId}/sheets/${props.feishu.tabId}/find")
                    .headers { headers -> headers.setBearerAuth(bearer.token) }
                    .body(BodyInserters.fromValue(search)).retrieve()
                    .bodyToMono<FindResponse>()
                    .zipWith(Mono.just(bearer))
            }.flatMap {
                val response = it.t1
                val bearer = it.t2
                val update = UpdatePayload("${props.feishu.tabId}!${response.range}", status.toString())
                client.put()
                    .uri("/sheets/v2/spreadsheets/${props.feishu.sheetId}/values")
                    .headers { headers -> headers.setBearerAuth(bearer.token) }
                    .body(BodyInserters.fromValue(update)).retrieve()
                    .bodyToMono<String>()
            }.block()
    }
}

private data class SearchPayload(val find: String, private val range: String) {
    @Suppress("unused")
    @JsonProperty("find_condition")
    val findCondition = FindCondition(range)
}

private data class FindCondition(
    val range: String,
    @JsonProperty("match_case")
    val matchCase: Boolean = true,
    @JsonProperty("match_entire_cell")
    val matchEntireCell: Boolean = true,
    @JsonProperty("search_by_regex")
    val searchByRegex: Boolean = false,
    @JsonProperty("include_formulas")
    val includeFormulas: Boolean = false
)

private data class FindResponse(
    val code: Int,
    val msg: String,
    val data: FindData?,
    val error: FindError?
) {
    val range: String
        get() {
            val logger = Loggers.getLogger(this::class.java)
            if (data != null) {
                with(data.findResult) {
                    if (rowsCount == 0) throw IllegalStateException("Found no conference with name")
                    else return "${matchedCells[0]}:${matchedCells[rowsCount - 1]}".replace('A', 'H')
                }
            } else if (error != null) {
                logger.error("Response returned an error: $error")
                throw IllegalStateException(msg)
            }
            logger.error("Impossible state: $msg")
            throw IllegalStateException(msg)
        }
}

private data class FindError(
    val subcode: Long,
    @JsonProperty("log_id")
    val logId: String
)

private data class FindData(
    @JsonProperty("find_result")
    val findResult: FindResult
)

private data class FindResult(
    @JsonProperty("matched_cells")
    val matchedCells: Array<String>,
    @JsonProperty("matched_formula_cells")
    val matchedFormulaCells: Array<String>,
    @JsonProperty("rows_count")
    val rowsCount: Int
)

private data class UpdatePayload(
    private val range: String,
    private val value: String
) {
    @Suppress("unused")
    val valueRange: Update = Update(range, arrayOf(arrayOf(value)))
}

private data class Update(
    val range: String,
    val values: Array<Array<String>>
)