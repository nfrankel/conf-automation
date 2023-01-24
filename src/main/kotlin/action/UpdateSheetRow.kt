package ch.frankel.conf.automation.action

import ch.frankel.conf.automation.conference
import ch.frankel.conf.automation.feishu.FeishuUpdateSheetRow
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate

class UpdateSheetRow(private val updateSheetRow: FeishuUpdateSheetRow) : JavaDelegate {

    override fun execute(execution: DelegateExecution) {
        updateSheetRow.execute(
            execution.conference,
            (execution.getVariable(BPMN_STATUS) as Status).toString()
        )
    }
}
