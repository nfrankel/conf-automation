package ch.frankel.conf.automation

import java.io.Serializable
import java.time.LocalDate

data class TrelloEvent(val action: Action) : Serializable

data class Action(
    val data: Data,
    val date: LocalDate,
    val id: String,
    val idMemberCreator: String,
    val type: String
) : Serializable

data class Data(
    val board: Board,
    val card: Card,
    val list: List?,
    val listAfter: List?,
    val listBefore: List?,
    val customField: CustomField?,
    val customFieldItem: Any?,
    val old: Old?
) : Serializable

data class Board(val id: String, val name: String, val shortLink: String) : Serializable
data class Card(val id: String, val idShort: Int, val name: String, val shortLink: String) : Serializable
data class CustomField(val id: String, val name: String, val type: String) : Serializable
data class List(val id: String, val name: String) : Serializable
data class Old(val name: String?, val idList: String?) : Serializable