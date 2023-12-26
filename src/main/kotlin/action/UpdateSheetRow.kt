package ch.frankel.conf.automation.action

import ch.frankel.conf.automation.conference
import ch.frankel.conf.automation.google.sheets.GoogleUpdateSheetRow
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.slf4j.LoggerFactory

class UpdateSheetRow(private val googleUpdateSheetRow: GoogleUpdateSheetRow) : JavaDelegate {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun execute(execution: DelegateExecution) {
        val transitionAsString = execution.getVariable(BPMN_TRANSITION) as String
        logger.info("[${execution.processInstanceId}] Read transition $transitionAsString")
        val transition = Transition.fromString(transitionAsString)
        val status = transition.end.toString()
        googleUpdateSheetRow.execute(execution.conference, status)
        logger.info("[${execution.processInstanceId}] Google Sheet updated with status $status")
    }
}
