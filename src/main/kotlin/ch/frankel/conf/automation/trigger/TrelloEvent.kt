package ch.frankel.conf.automation.trigger

import java.io.Serializable

data class TrelloEvent(
    val action: Action
): Serializable