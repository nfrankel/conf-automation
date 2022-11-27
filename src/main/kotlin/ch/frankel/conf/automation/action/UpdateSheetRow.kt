package ch.frankel.conf.automation.action

import ch.frankel.conf.automation.BPMN_STATUS
import ch.frankel.conf.automation.Status
import ch.frankel.conf.automation.feishu.FeishuUpdateSheetRow
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate

class UpdateSheetRow(private val updateSheetRow: FeishuUpdateSheetRow) : JavaDelegate {

    override fun execute(execution: DelegateExecution) {
        updateSheetRow.execute(
            execution.conference,
            execution.getVariable(BPMN_STATUS) as Status
        )
    }
}
