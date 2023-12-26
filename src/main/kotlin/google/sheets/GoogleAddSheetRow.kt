package ch.frankel.conf.automation.google.sheets

import ch.frankel.conf.automation.SheetsProperties
import ch.frankel.conf.automation.action.*
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.ValueRange
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class GoogleAddSheetRow(private val client: Sheets, private val props: SheetsProperties, private val speaker: String) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun execute(conference: Conference) {
        client.spreadsheets().values()
            .append(props.id, "${conference.endDate.year}!A1:B1", conference.toValueRange(speaker))
            .setValueInputOption("USER_ENTERED")
            .execute()
        logger.info("Row ${conference.name} added to Google Sheet ${props.id}")
    }

    private fun Conference.toValueRange(speaker: String) = ValueRange().apply {
        setValues(
            listOf(
                listOf(
                    "=HYPERLINK(\"$website\", \"$name\")",
                    speaker,
                    "",
                    location,
                    startDate.formatted,
                    endDate.formatted,
                    "Yes",
                    "Submitted",
                )
            )
        )
    }

    private val LocalDate.formatted: String
        get() = DateTimeFormatter.ISO_LOCAL_DATE.format(this)
}
