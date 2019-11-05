package ch.frankel.conf.automation.trigger

import java.io.Serializable

data class Board(
    val id: String,
    val name: String,
    val shortLink: String
): Serializable