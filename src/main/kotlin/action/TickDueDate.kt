package ch.frankel.conf.automation.action

import ch.frankel.conf.automation.trello.TrelloTickDueDate
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate

class TickDueDate(private val tickDueDate: TrelloTickDueDate) : JavaDelegate {
    override fun execute(execution: DelegateExecution) {
        val processInstanceId = execution.processInstanceId
        val cardId = execution.businessKey
        tickDueDate.execute(processInstanceId, cardId)
    }
}
