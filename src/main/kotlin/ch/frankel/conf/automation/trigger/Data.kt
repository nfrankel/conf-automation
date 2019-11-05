package ch.frankel.conf.automation.trigger

import java.io.Serializable

data class Data(
    val board: Board,
    val card: Card,
    val list: List?,
    val listAfter: List?,
    val listBefore: List?,
    val customField: CustomField?,
    val customFieldItem: Any?,
    val old: Old?
): Serializable