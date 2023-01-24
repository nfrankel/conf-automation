package ch.frankel.conf.automation.action

import ch.frankel.conf.automation.feishu.FeishuAddSheetRow
import ch.frankel.conf.automation.conference
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate

class AddSheetRow(private val addSheetRow: FeishuAddSheetRow) : JavaDelegate {

    override fun execute(execution: DelegateExecution) {
        addSheetRow.execute(execution.conference)
    }
}

