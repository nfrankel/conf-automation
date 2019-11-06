package ch.frankel.conf.automation.action

import ch.frankel.conf.automation.AppProperties
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.ValueRange
import org.camunda.bpm.engine.delegate.DelegateExecution
import java.time.format.DateTimeFormatter

class AddSheetRow(private val props: AppProperties,
                  fieldsInitializer: CustomFieldsInitializer) : GoogleAction(props, fieldsInitializer) {

    override fun execute(execution: DelegateExecution) {
        val client = Sheets
            .Builder(TRANSPORT, JSON_FACTORY, credential)
            .build()
        val firstEmptyRow = getFirstEmptyRow(client)
        updateInPlace(client, execution, firstEmptyRow)
    }

    private fun updateInPlace(client: Sheets, execution: DelegateExecution, row: Int) {
        val conference = extractConference(execution)
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
                val range = ValueRange().apply {
                    setValues(listOf(values))
                }
                client.Spreadsheets().values()
                    .update(props.google.sheetId, "$col$row:$col", range)
                    .setValueInputOption("USER_ENTERED")
                    .setIncludeValuesInResponse(true)
                    .execute()
            }
    }

    private fun getFirstEmptyRow(client: Sheets): Int {
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