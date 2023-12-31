package ch.frankel.conf.automation.google.sheets

import ch.frankel.conf.automation.SheetsProperties
import ch.frankel.conf.automation.action.Conference
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.ValueRange
import org.slf4j.LoggerFactory

class GoogleUpdateSheetRow(
    private val client: Sheets,
    private val props: SheetsProperties,
    private val speaker: String
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun execute(conference: Conference, status: String) {
        val rows: ValueRange = client.spreadsheets()
            .values()[props.id, "${conference.endDate.year}!A3:H"]
            .execute()
        val typedResult = Result(rows.values)
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
        logger.info("Row ${conference.name} updated with $status in Google Sheet ${props.id}")
    }

    private fun Result.findSubmittedRange(speaker: String, conference: Conference): Pair<Int, String> {
        val indices = findRowIndices(speaker, conference)
        // Assumption: all rows are adjacent
        val firstIndex = indices.first()
        val lastIndex = indices.last()
        return indices.size to "H$firstIndex:H$lastIndex"
    }
}
