package ch.frankel.conf.automation.action

import ch.frankel.conf.automation.BPMN_CONFERENCE
import ch.frankel.conf.automation.google.sheets.GoogleDeleteSheetRow
import kotlinx.serialization.json.Json
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.slf4j.LoggerFactory

class DeleteSheetRow(private val googleDeleteSheetRow: GoogleDeleteSheetRow) : JavaDelegate {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun execute(execution: DelegateExecution) {
        val stringifiedConference = execution.getVariable(BPMN_CONFERENCE) as String
        val conference = Json.decodeFromString<Conference>(stringifiedConference)
        googleDeleteSheetRow.execute(conference)
        logger.info("[${execution.processInstanceId}] ${conference.name} deleted from Google Sheet")
    }
}
