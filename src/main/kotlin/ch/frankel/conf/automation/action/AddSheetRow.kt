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
        val values = mapOf(
            "A" to listOf(hyperlinked),
            "B" to listOf("Nicolas Fränkel"),
            "E" to listOf(conference.location),
            "H" to listOf(formattedStartDate),
            "I" to listOf(formattedEndDate),
            "O" to listOf("Submitted"))

        values.forEach { (col, values) ->
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
            .get(props.google.sheetId, "A3:A")
            .execute()
            .values
        // Range starts from 1
        // And there's one blank line that isn't computed by Google Sheets
        return (values.last() as Collection<Any>).size + 3
    }
}