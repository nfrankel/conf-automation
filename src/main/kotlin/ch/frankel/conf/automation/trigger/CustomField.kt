package ch.frankel.conf.automation.trigger

import java.io.Serializable

data class CustomField(
    val id: String,
    val name: String,
    val type: String
): Serializable