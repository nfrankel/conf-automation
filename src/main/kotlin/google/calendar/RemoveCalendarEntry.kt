package ch.frankel.conf.automation.google.calendar

import ch.frankel.conf.automation.CalendarProperties
import ch.frankel.conf.automation.getConference
import com.google.api.services.calendar.Calendar
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.slf4j.LoggerFactory

class RemoveCalendarEntry(private val client: Calendar, private val props: CalendarProperties) : JavaDelegate {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun execute(execution: DelegateExecution) {
        val conference = execution.getConference()
        logger.info("[${execution.processInstanceId}] Trying to remove calendar entry for ${conference.name}")
        findCalendarEntry(client, props, conference)?.let {
            client.events().delete(props.id, it.id).execute()
            logger.info("[${execution.processInstanceId}] Calendar entry removed for ${conference.name}")
        }
    }
}