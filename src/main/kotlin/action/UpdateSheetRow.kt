package ch.frankel.conf.automation.action

import ch.frankel.conf.automation.conference
import ch.frankel.conf.automation.google.sheets.GoogleUpdateSheetRow
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate

class UpdateSheetRow(private val googleUpdateSheetRow: GoogleUpdateSheetRow) : JavaDelegate {

    override fun execute(execution: DelegateExecution) {
        val transitionAsString = execution.getVariable(BPMN_TRANSITION) as String
        val transition = Transition.fromString(transitionAsString)
        val status = transition.end.toString()
        googleUpdateSheetRow.execute(execution.conference, status)
    }
}
