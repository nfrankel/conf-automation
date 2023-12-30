package ch.frankel.conf.automation.google.sheets

import ch.frankel.conf.automation.SheetsProperties
import ch.frankel.conf.automation.action.Conference
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.*
import org.slf4j.LoggerFactory

class GoogleDeleteSheetRow(
    private val client: Sheets,
    private val props: SheetsProperties,
    private val speaker: String,
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun execute(conference: Conference) {
        val rows: ValueRange = client.spreadsheets()
            .values()[props.id, "${conference.endDate.year}!A3:H"]
            .execute()
        val typedResult = Result(rows.values)
        val rowIndicesToUpdate = typedResult.findRowIndices(speaker, conference)
        val sheetId = getSheetId(conference)
        val response = client.spreadsheets()
            .batchUpdate(
                props.id,
                BatchUpdateSpreadsheetRequest().apply {
                    requests = listOf(Request().apply {
                        deleteDimension = DeleteDimensionRequest().apply {
                            range = DimensionRange().apply {
                                this.sheetId = sheetId
                                dimension = "ROWS"
                                startIndex = rowIndicesToUpdate.first()
                                endIndex = rowIndicesToUpdate.last() + 1 // End index is exclusive
                            }
                        }
                    })
                }
            ).execute()
        response.replies.forEach {
            logger.info("Response: $it")
        }
        logger.info("Row(s) $rowIndicesToUpdate containing ${conference.name} deleted in Google Sheet")
    }

    private fun getSheetId(conference: Conference) = client.spreadsheets()[props.id].execute().sheets.single {
        it.properties.title == conference.endDate.year.toString()
    }.properties.sheetId

    class Result(values: Collection<Any>) {
        private val majorDimension: String
        private val range: String
        private val rows: List<ResultRow>

        init {
            val wrapper: List<Any> = ArrayList(values)
            majorDimension = wrapper[0] as String
            range = wrapper[1] as String
            rows = (wrapper[2] as List<List<Any>>)
                .filter { it.isNotEmpty() }
                .mapIndexed { index, values -> ResultRow(index + 3, values) }
        }

        fun findRowIndices(speaker: String, conference: Conference) = rows
            .filter { it.isMatch(speaker, conference.name, "Submitted") }
            .map { it.index }

        override fun toString() = "Sheet(dimension='$majorDimension', range='$range', rows=$rows)"
    }

    class ResultRow(val index: Int, values: List<Any>) {

        val name = values[0]
        val speaker = values[1]
        val talk = values[2]
        val country = values[3]
        val startDate = values[4]
        val endDate = values[5]
        val related = values[6]
        val status = values[7]

        fun isMatch(speaker: String, conference: String, status: String) =
            this.name == conference && this.speaker == speaker && this.status == status

        override fun toString() = "[$index]->(name=$name, speaker=$speaker, status=$status)"
    }
}
