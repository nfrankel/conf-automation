package ch.frankel.conf.automation.facade

import ch.frankel.conf.automation.getConference
import ch.frankel.conf.automation.delegate.calendar.UpdateCalendarEntryDelegate
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate

class UpdateCalendarEntry(private val updateCalendarEntryDelegate: UpdateCalendarEntryDelegate): JavaDelegate {

    override fun execute(execution: DelegateExecution) {
        val conference = execution.getConference()
        updateCalendarEntryDelegate.execute(conference)
    }
}