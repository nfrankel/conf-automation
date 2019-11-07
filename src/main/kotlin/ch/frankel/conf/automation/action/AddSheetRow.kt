package ch.frankel.conf.automation.action

import ch.frankel.conf.automation.AppProperties
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.ValueRange
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import java.time.format.DateTimeFormatter

class AddSheetRow(private val props: AppProperties) : JavaDelegate {

    private val client = Sheets
        .Builder(TRANSPORT, JSON_FACTORY, props.toCredential())
        .build()

    override fun execute(execution: DelegateExecution) {
        val firstEmptyRow = getFirstEmptyRow()
        updateInPlace(execution, firstEmptyRow)
    }

    private fun updateInPlace(execution: DelegateExecution, row: Int) {
        val conference = execution.conference
        val hyperlinked = "=HYPERLINK(\"${conference.website}\",\"${conference.name}\")"
        val formattedStartDate = DateTimeFormatter.ISO_LOCAL_DATE.format(conference.startDate)
        val formattedEndDate = DateTimeFormatter.ISO_LOCAL_DATE.format(conference.endDate)
        mapOf(
            Column.CONFERENCE.col to listOf(hyperlinked),
            Column.SPEAKER.col to listOf(props.speaker),
            Column.LOCATION_COUNTRY.col to listOf(conference.location),
            Column.START_DATE.col to listOf(formattedStartDate),
            Column.END_DATE.col to listOf(formattedEndDate),
            Column.STATUS.col to listOf("Submitted"))
            .forEach { (col, values) ->
                val range = ValueRange().setValues(listOf(values))
                client.Spreadsheets().values()
                    .update(props.google.sheetId, "$col$row:$col", range)
                    .setValueInputOption("USER_ENTERED")
                    .setIncludeValuesInResponse(true)
                    .execute()
            }
    }

    private fun getFirstEmptyRow(): Int {
        val values = client.Spreadsheets().values()
            .get(props.google.sheetId, "${Column.CONFERENCE.col}3:${Column.CONFERENCE.col}")
            .execute()
            .values
        return (values.last() as Collection<Any>).size + ADDITIONAL_ROWS
    }
}

private enum class Column(val col: String) {
    CONFERENCE("A"),
    SPEAKER("B"),
    LOCATION_COUNTRY("E"),
    START_DATE("H"),
    END_DATE("I"),
    STATUS("O")
}

// Range starts from 1
// And there's one blank line that isn't computed by Google Sheets
private const val ADDITIONAL_ROWS = 3