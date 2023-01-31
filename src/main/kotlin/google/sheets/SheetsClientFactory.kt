package ch.frankel.conf.automation.google.sheets

import ch.frankel.conf.automation.AppProperties
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials


class SheetsClientFactory(val props: AppProperties) {

    fun createInstance(): Sheets {
        val credentials = GoogleCredentials.fromStream(props.google.sheets.json.byteInputStream())
            .createScoped(SheetsScopes.SPREADSHEETS)
        return Sheets
            .Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                HttpCredentialsAdapter(credentials)
            )
            .setApplicationName(props.name)
            .build()
    }
}
