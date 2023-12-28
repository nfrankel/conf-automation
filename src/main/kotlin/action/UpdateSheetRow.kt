package ch.frankel.conf.automation.action

import ch.frankel.conf.automation.BPMN_CONFERENCE
import ch.frankel.conf.automation.google.sheets.GoogleUpdateSheetRow
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.slf4j.LoggerFactory

class UpdateSheetRow(private val googleUpdateSheetRow: GoogleUpdateSheetRow) : JavaDelegate {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun execute(execution: DelegateExecution) {
        val status = execution.getVariableLocal("status") as String
        logger.info("[${execution.processInstanceId}] Read status $status")
        googleUpdateSheetRow.execute(execution.getVariable(BPMN_CONFERENCE) as Conference, status)
        logger.info("[${execution.processInstanceId}] Google Sheet updated with status $status")
    }
}
