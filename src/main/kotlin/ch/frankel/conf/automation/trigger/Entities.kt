package ch.frankel.conf.automation.trigger

import java.io.Serializable

data class Entities(
    val card: Card,
    val list: List,
    val memberCreator: MemberCreator
): Serializable