package ch.frankel.conf.automation.action

import ch.frankel.conf.automation.trello.TrelloRemoveDueDate
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate

class RemoveDueDate(private val removeDueDate: TrelloRemoveDueDate) : JavaDelegate {
    override fun execute(execution: DelegateExecution) {
        val processInstanceId = execution.processInstanceId
        val cardId = execution.businessKey
        removeDueDate.execute(processInstanceId, cardId)
    }
}
