package ch.frankel.conf.automation

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable
import java.time.LocalDate

data class TrelloEvent(val action: Action) : Serializable {
    val cardId = this.action.data.card.id
}

data class Action(
    val data: Data,
    val date: LocalDate,
    val id: String,
    val idMemberCreator: String,
    val type: ActionType
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

enum class ActionType {
    @JsonProperty("updateCard")
    UPDATE_CARD,
    @JsonProperty("updateCustomFieldItem")
    UPDATE_CUSTOM_FIELD_ITEM,
    @JsonProperty("copyCard")
    COPY_CARD,
    @JsonProperty("addLabelToCard")
    ADD_LABEL_TO_CARD
}
