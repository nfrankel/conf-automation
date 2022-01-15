package ch.frankel.conf.automation.action

import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate

object NoOp : JavaDelegate {

    override fun execute(execution: DelegateExecution) {
        // NO-OP
    }
}