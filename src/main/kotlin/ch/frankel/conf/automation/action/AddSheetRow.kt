package ch.frankel.conf.automation.action

import ch.frankel.conf.automation.AppProperties
import com.google.api.services.sheets.v4.model.ValueRange
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate

class AddSheetRow(private val props: AppProperties) : JavaDelegate {

    private val client = props.sheetsClient

    override fun execute(execution: DelegateExecution) {
        val firstEmptyRow = getFirstEmptyRow()
        updateInPlace(execution, firstEmptyRow)
    }

    private fun updateInPlace(execution: DelegateExecution, row: Int) {
        val conference = execution.conference
        mapOf(
            Column.CONFERENCE to conference.hyperlink,
            Column.SPEAKER to props.speaker,
            Column.LOCATION_COUNTRY to conference.location,
            Column.START_DATE to conference.startDate.formatted,
            Column.END_DATE to conference.endDate.formatted,
            Column.STATUS to "Submitted")
            .forEach { (col, values) ->
                val range = ValueRange().setValues(listOf(values))
                client.Spreadsheets().values()
                    .update(props.google.sheetId, "$col$row:$col", range)
                    .setValueInputOption("USER_ENTERED")
                    .setIncludeValuesInResponse(true)
                    .execute()
            }
    }

    private val Conference.hyperlink: String
        get() = "=HYPERLINK(\"$website\",\"$name\")"

    private infix fun Column.to(string: String): Pair<String, List<String>> {
        return col to listOf(string)
    }

    private fun getFirstEmptyRow(): Int {
        // Range starts from 1
        // And there's one blank line that isn't computed by Google Sheets
        val additionalRows = 3
        val values = client.Spreadsheets().values()
            .get(props.google.sheetId, "${Column.CONFERENCE.col}3:${Column.CONFERENCE.col}")
            .execute()
            .values
        return (values.last() as Collection<*>).size + additionalRows
    }

    private enum class Column(val col: String) {
        CONFERENCE("A"),
        SPEAKER("B"),
        LOCATION_COUNTRY("E"),
        START_DATE("H"),
        END_DATE("I"),
        STATUS("O")
    }
}

