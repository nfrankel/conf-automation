package ch.frankel.conf.automation.action

import ch.frankel.conf.automation.*
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.bodyToMono

class UpdateSheetRow(private val props: AppProperties) : JavaDelegate {

    override fun execute(execution: DelegateExecution) {
        props.bearerToken()?.let { token ->
            val conference = execution.conference
            val search = SearchPayload(conference.name, "${props.feishu.tabId}!A3:A399")
            feishuClient
                .post()
                .uri("/sheets/v3/spreadsheets/${props.feishu.sheetId}/sheets/${props.feishu.tabId}/find")
                .headers { headers -> headers.setBearerAuth(token) }
                .body(
                    BodyInserters.fromValue(search)
                ).retrieve()
                .bodyToMono<FindResponse>()
                .flatMap {
                    val update = UpdatePayload("${props.feishu.tabId}!${it.range}", execution.status.toString())
                    feishuClient
                        .put()
                        .uri("/sheets/v2/spreadsheets/${props.feishu.sheetId}/values")
                        .headers { headers -> headers.setBearerAuth(token) }
                        .body(
                            BodyInserters.fromValue(update)
                        ).retrieve()
                        .bodyToMono<String>()
                }.block()
        }
    }

    private data class SearchPayload(val find: String, private val range: String) {
        @Suppress("unused")
        val find_condition = FindCondition(range)
    }

    private data class FindCondition(
        val range: String,
        val match_case: Boolean = true,
        val match_entire_cell: Boolean = true,
        val search_by_regex: Boolean = false,
        val include_formulas: Boolean = false
    )

    private data class FindResponse(
        val code: Int,
        val data: FindData
    ) {
        val range: String
            get() {
                with(data.find_result) {
                    if (rows_count == 0) throw IllegalStateException("Found no conference with name")
                    else return "${matched_cells[0]}:${matched_cells[rows_count - 1]}".replace('A', 'H')
                }
            }
    }

    private data class FindData(
        val find_result: FindResult
    )

    private data class FindResult(
        val matched_cells: Array<String>,
        val matched_formula_cells: Array<String>,
        val rows_count: Int
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

    private val DelegateExecution.status: Status
        get() = getVariable(BPMN_STATUS) as Status
}