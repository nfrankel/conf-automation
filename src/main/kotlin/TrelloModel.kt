package ch.frankel.conf.automation

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName


@Serializable
data class TrelloEvent(val action: Action, val model: Model, val webhook: Webhook) {
    val cardId = this.action.data.card.id

    override fun toString() = """
        | TrelloEvent(
        |   action=$action,
        |   model=$model,
        |   webhook=$webhook
        | )
    """.trimIndent()
}

@Serializable
data class Action(
    val data: Data,
    val date: Instant,
    val id: String,
    val idMemberCreator: String,
    val type: ActionType
)

@Serializable
data class Data(
    val board: Board,
    val card: Card,
    val list: List?,
    val listAfter: List?,
    val listBefore: List?,
    val customField: CustomField?,
    val customFieldItem: TrelloCustomFieldItem?,
    val old: Old?
)

@Serializable
data class Board(val id: String, val name: String, val shortLink: String)
@Serializable
data class Card(val id: String, val idShort: Int, val name: String, val shortLink: String)
@Serializable
data class CustomField(val id: String, val name: String, val type: String)
@Serializable
data class TrelloCustomFieldItem(val id: String, val value: Value, val idValue: String, val idCustomField: String, val idModel: String, val modelType: String)
@Serializable
data class Value(val text: String)
@Serializable
data class List(val id: String, val name: String)
@Serializable
data class Old(val name: String?, val idList: String?)

@Serializable
enum class ActionType {
    @SerialName("updateCard")
    UPDATE_CARD,
    @SerialName("updateCustomFieldItem")
    UPDATE_CUSTOM_FIELD_ITEM,
    @SerialName("copyCard")
    COPY_CARD,
    @SerialName("addLabelToCard")
    ADD_LABEL_TO_CARD
}

@Serializable
data class Model(
    val id: String,
    val name: String,
    val closed: Boolean,
    val idOrganization: String,
    val url: String,
    val shortUrl: String,
)

@Serializable
data class Webhook(
    val id: String,
    val description: String,
    val idModel: String,
    val callbackURL: String,
    val active: Boolean,
    val consecutiveFailures: Int,
    val firstConsecutiveFailDate: Instant?,
)
