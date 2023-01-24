package ch.frankel.conf.automation.action

import ch.frankel.conf.automation.conference
import ch.frankel.conf.automation.feishu.FeishuUpdateSheetRow
import ch.frankel.conf.automation.google.sheets.GoogleUpdateSheetRow
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate

class UpdateSheetRow(
    private val updateSheetRow: FeishuUpdateSheetRow,
    private val googleUpdateSheetRow: GoogleUpdateSheetRow
) : JavaDelegate {

    override fun execute(execution: DelegateExecution) {
        val status = (execution.getVariable(BPMN_STATUS) as Status).toString()
        updateSheetRow.execute(execution.conference, status)
        googleUpdateSheetRow.execute(execution.conference, status)
    }
}
