package ch.frankel.conf.automation.action

import ch.frankel.conf.automation.BPMN_CONFERENCE
import ch.frankel.conf.automation.google.sheets.GoogleUpdateSheetRow
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.slf4j.LoggerFactory

class UpdateSheetRow(private val googleUpdateSheetRow: GoogleUpdateSheetRow, private val status: String) : JavaDelegate {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun execute(execution: DelegateExecution) {
        logger.info("[${execution.processInstanceId}] Read message $status")
        googleUpdateSheetRow.execute(execution.getVariable(BPMN_CONFERENCE) as Conference, status)
        logger.info("[${execution.processInstanceId}] Google Sheet updated with status $status")
    }
}
