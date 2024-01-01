package ch.frankel.conf.automation.facade

import ch.frankel.conf.automation.getConference
import ch.frankel.conf.automation.delegate.sheets.AddSheetRowDelegate
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.slf4j.LoggerFactory

class AddSheetRow(private val addSheetRowDelegate: AddSheetRowDelegate) : JavaDelegate {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun execute(execution: DelegateExecution) {
        val conference = execution.getConference()
        addSheetRowDelegate.execute(conference)
        logger.info("[${execution.processInstanceId}] ${conference.name} added to Google Sheet")
    }
}
