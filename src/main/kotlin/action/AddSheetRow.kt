package ch.frankel.conf.automation.action

import ch.frankel.conf.automation.BPMN_CONFERENCE
import ch.frankel.conf.automation.google.sheets.GoogleAddSheetRow
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate

class AddSheetRow(private val googleAddSheetRow: GoogleAddSheetRow) : JavaDelegate {

    override fun execute(execution: DelegateExecution) {
        googleAddSheetRow.execute(execution.getVariable(BPMN_CONFERENCE) as Conference)
    }
}
