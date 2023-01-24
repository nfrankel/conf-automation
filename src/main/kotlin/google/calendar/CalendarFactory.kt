package ch.frankel.conf.automation.google.calendar

import ch.frankel.conf.automation.AppProperties
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials


class CalendarFactory(val props: AppProperties) {

    fun createInstance(): Calendar {
        val credentials = GoogleCredentials.fromStream(props.google.calendar.json.byteInputStream())
            .createScoped(CalendarScopes.CALENDAR)
        return Calendar
            .Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                HttpCredentialsAdapter(credentials)
            )
            .setApplicationName(props.name)
            .build()
    }
}
