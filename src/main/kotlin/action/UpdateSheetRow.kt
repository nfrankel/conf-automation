package ch.frankel.conf.automation.action

import ch.frankel.conf.automation.BPMN_CONFERENCE
import ch.frankel.conf.automation.Message
import ch.frankel.conf.automation.google.sheets.GoogleUpdateSheetRow
import kotlinx.serialization.json.Json
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.slf4j.LoggerFactory

class UpdateSheetRow(private val googleUpdateSheetRow: GoogleUpdateSheetRow, private val status: Message) : JavaDelegate {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun execute(execution: DelegateExecution) {
        val conferenceAsJson = execution.getVariable(BPMN_CONFERENCE) as String
        val conference = Json.decodeFromString<Conference>(conferenceAsJson)
        googleUpdateSheetRow.execute(conference, status.toString())
        logger.info("[${execution.processInstanceId}] Google Sheet updated for ${conference.name} with status $status")
    }
}
