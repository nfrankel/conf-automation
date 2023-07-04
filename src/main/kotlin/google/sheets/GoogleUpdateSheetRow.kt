package ch.frankel.conf.automation.google.sheets

import ch.frankel.conf.automation.SheetsProperties
import ch.frankel.conf.automation.action.Conference
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.ValueRange

class GoogleUpdateSheetRow(
    private val client: Sheets,
    private val props: SheetsProperties,
    private val speaker: String
) {

    fun execute(conference: Conference, status: String) {
        val getResult: ValueRange = client.spreadsheets().values()
            .get(props.id, "A3:H")
            .execute()
        val typedResult = Result(getResult.values)
        val rangeToUpdate = typedResult.findSubmittedRange(speaker, conference)
        val valueRange = ValueRange().apply {
            majorDimension = "COLUMNS"
            val values = List(rangeToUpdate.first) { status }
            setValues(listOf(values))
        }
        client.spreadsheets().values()
            .update(props.id, rangeToUpdate.second, valueRange)
            .setValueInputOption("USER_ENTERED")
            .execute()
    }

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

        fun findSubmittedRange(speaker: String, conference: Conference): Pair<Int, String> {
            val indices = rows
                .filter { it.isMatch(speaker, conference.name, "Submitted") }
                .map { it.index }
            // Assumption: all rows are adjacent
            val firstIndex = indices.first()
            val lastIndex = indices.last()
            return indices.size to "H$firstIndex:H$lastIndex"
        }

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
