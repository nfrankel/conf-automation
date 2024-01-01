package ch.frankel.conf.automation.action

import ch.frankel.conf.automation.getConference
import ch.frankel.conf.automation.google.sheets.GoogleAddSheetRow
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.slf4j.LoggerFactory

class AddSheetRow(private val googleAddSheetRow: GoogleAddSheetRow) : JavaDelegate {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun execute(execution: DelegateExecution) {
        val conference = execution.getConference()
        googleAddSheetRow.execute(conference)
        logger.info("[${execution.processInstanceId}] ${conference.name} added to Google Sheet")
    }
}
