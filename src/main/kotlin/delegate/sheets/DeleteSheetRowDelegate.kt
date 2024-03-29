package ch.frankel.conf.automation.delegate.sheets

import ch.frankel.conf.automation.SheetsProperties
import ch.frankel.conf.automation.facade.Conference
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.*
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.slf4j.LoggerFactory

class DeleteSheetRowDelegate(
    private val client: Sheets,
    private val props: SheetsProperties,
    private val speaker: String,
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun execute(conference: Conference) {
        val rows: ValueRange = client.spreadsheets()
            .values()[props.id, "${conference.endDate.toLocalDateTime(TimeZone.UTC).year}!A3:H"]
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
        it.properties.title == conference.endDate.toLocalDateTime(TimeZone.UTC).year.toString()
    }.properties.sheetId
}
