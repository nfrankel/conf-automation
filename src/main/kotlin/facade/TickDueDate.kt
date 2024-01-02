package ch.frankel.conf.automation.facade

import ch.frankel.conf.automation.delegate.trello.TickDueDateDelegate
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate

class TickDueDate(private val tickDueDate: TickDueDateDelegate, private val tick: Boolean) : JavaDelegate {

    override fun execute(execution: DelegateExecution) {
        val processInstanceId = execution.processInstanceId
        val cardId = execution.businessKey
        tickDueDate.execute(processInstanceId, cardId, tick)
    }
}
