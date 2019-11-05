package ch.frankel.conf.automation.trigger

import java.io.Serializable

data class Card(
    val id: String,
    val idShort: Int,
    val name: String,
    val shortLink: String
): Serializable