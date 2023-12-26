package ch.frankel.conf.automation.action

import ch.frankel.conf.automation.TrelloEvent
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.slf4j.LoggerFactory

class ComputeEventTransition : JavaDelegate {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun execute(it: DelegateExecution) {
        logger.info("[${it.processInstanceId}] Received event ${it.trelloEvent}")
        with(it.trelloEvent.transition) {
            it.setVariable(BPMN_TRANSITION, this.toString())
            logger.info("[${it.processInstanceId}] Event transition is $this")
        }
    }

    private val TrelloEvent.transition: Transition
        get() {
            val before = action.data.listBefore
            val after = action.data.listAfter
            val key = before?.name to after?.name
            return transitionStatusMatrix.getOrDefault(key, Transition.Irrelevant)
        }
}
