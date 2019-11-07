package ch.frankel.conf.automation.action

import ch.frankel.conf.automation.*
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.ValueRange
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import java.lang.IllegalStateException
import java.time.format.DateTimeFormatter

class UpdateSheetRow(private val props: AppProperties) : JavaDelegate {

    private val client = Sheets
        .Builder(TRANSPORT, JSON_FACTORY, props.toCredential())
        .build()

    override fun execute(execution: DelegateExecution) {
        val formattedStartDate = DateTimeFormatter.ISO_LOCAL_DATE.format(execution.conference.startDate)
        val formattedEndDate = DateTimeFormatter.ISO_LOCAL_DATE.format(execution.conference.endDate)
        val status = when(execution.getVariable("type") as EventType) {
            EventType.Rejection -> "Rejected"
            EventType.Acceptance -> "Accepted"
            else -> throw IllegalStateException("Unknown event type")
        }
        client.Spreadsheets().values()
            .get(props.google.sheetId, "A:I")
            .execute()
            .getValues()
            .mapIndexed { index, it -> index to it }
            .filter {
                val data = it.second
                data.size > 7
                    && (data[0] as String) == execution.conference.name
                    && (data[7] as String) == formattedStartDate
                    && (data[8] as String) == formattedEndDate
            }.forEach {
                val value = ValueRange().setValues(listOf(listOf(status)))
                val index = it.first + 1
                client.Spreadsheets().values()
                    .update(props.google.sheetId, "O$index:O$index", value)
                    .setValueInputOption("USER_ENTERED")
                    .execute()
            }
    }
}