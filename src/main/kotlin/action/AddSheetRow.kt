package ch.frankel.conf.automation.action

import ch.frankel.conf.automation.BPMN_CONFERENCE
import ch.frankel.conf.automation.google.sheets.GoogleAddSheetRow
import kotlinx.serialization.json.Json
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.slf4j.LoggerFactory

class AddSheetRow(private val googleAddSheetRow: GoogleAddSheetRow) : JavaDelegate {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun execute(execution: DelegateExecution) {
        val conferenceAsJson = execution.getVariable(BPMN_CONFERENCE) as String
        val conference = Json.decodeFromString<Conference>(conferenceAsJson)
        googleAddSheetRow.execute(conference)
        logger.info("[${execution.processInstanceId}] ${conference.name} add to Google Sheet")
    }
}
