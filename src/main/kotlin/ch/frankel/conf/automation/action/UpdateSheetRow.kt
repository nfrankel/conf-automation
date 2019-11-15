package ch.frankel.conf.automation.action

import ch.frankel.conf.automation.*
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.ValueRange
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate

class UpdateSheetRow(private val props: AppProperties) : JavaDelegate {

    private val client = Sheets
        .Builder(TRANSPORT, JSON_FACTORY, props.credential)
        .setApplicationName(props.name)
        .build()

    override fun execute(execution: DelegateExecution) {
        val formattedStartDate = execution.conference.startDate.formatted
        val formattedEndDate = execution.conference.endDate.formatted
        val status =
            if (execution.status == Status.Rejected ||
                execution.status == Status.Accepted) execution.status.toString()
            else throw IllegalStateException("Unknown event status")
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

    private val DelegateExecution.status: Status
        get() = getVariable(BPMN_STATUS) as Status
}