package ch.frankel.conf.automation.google.calendar

import ch.frankel.conf.automation.CalendarProperties
import ch.frankel.conf.automation.getConference
import com.google.api.services.calendar.Calendar
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate

class UpdateCalendarEntry(private val client: Calendar, private val props: CalendarProperties) : JavaDelegate {

    override fun execute(execution: DelegateExecution) {
        val conference = execution.getConference()
        findCalendarEntry(client, props, conference)?.let {
            it.colorId = Color.Default.id
            it.transparency = Availability.Busy.value
            client.events()
                .update(props.id, it.id, it).execute()
        }
    }
}