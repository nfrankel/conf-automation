package ch.frankel.conf.automation.trigger

import java.io.Serializable
import java.time.LocalDate

data class Action(
    val data: Data,
    val date: LocalDate,
    val id: String,
    val idMemberCreator: String,
    val memberCreator: MemberCreator,
    val type: String
): Serializable