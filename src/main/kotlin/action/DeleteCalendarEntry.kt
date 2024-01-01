package ch.frankel.conf.automation.action

import ch.frankel.conf.automation.getConference
import ch.frankel.conf.automation.google.calendar.AddCalendarEntryDelegate
import ch.frankel.conf.automation.google.calendar.RemoveCalendarEntryDelegate
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate

class DeleteCalendarEntry(private val deleteCalendarEntryDelegate: RemoveCalendarEntryDelegate) : JavaDelegate {

    override fun execute(execution: DelegateExecution) {
        val conference = execution.getConference()
        deleteCalendarEntryDelegate.execute(conference)
    }
}