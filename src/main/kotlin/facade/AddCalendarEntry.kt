package ch.frankel.conf.automation.facade

import ch.frankel.conf.automation.getConference
import ch.frankel.conf.automation.delegate.calendar.AddCalendarEntryDelegate
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate

class AddCalendarEntry(private val addCalendarEntryDelegate: AddCalendarEntryDelegate) : JavaDelegate {

    override fun execute(execution: DelegateExecution) {
        val conference = execution.getConference()
        addCalendarEntryDelegate.execute(conference)
    }
}