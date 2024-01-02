package ch.frankel.conf.automation.facade

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
class Conference private constructor(
    val name: String,
    val location: String,
    val website: String,
    val startDate: Instant,
    val endDate: Instant,
) {

    companion object {
        fun from(title: String, fields: List<Field<out Any>>): Conference {
            val name: String
            val location: String
            val regex = "(.*) \\d{4} \\((.*)\\)".toRegex()
            val groups = regex.find(title)?.groupValues
            when {
                groups != null && groups.size > 2 -> {
                    name = groups[1]
                    location = groups[2]
                }
                groups != null && groups.size == 2 -> {
                    name = groups[1]
                    location = ""
                }
                else -> {
                    name = ""
                    location = ""
                }
            }

            val startDate: Instant = Instant.parse((fields.find { it is StartDate } as StartDate).value)
            val endDate: Instant = Instant.parse((fields.find { it is EndDate } as EndDate).value)
            val website = (fields.find { it is Website } as Website).value
            return Conference(name, location, website, startDate, endDate)
        }
    }

    override fun toString() = "Conference(summary=$name,location=$location,website=\"$website\",startDate=$startDate,endDate=$endDate"
}

interface Field<T> {
    val name: String
    val value: T
}

sealed class AbstractDate(item: CustomFieldItem) : Field<String> {
    override val value: String = item.value?.date as String
}

class StartDate(item: CustomFieldItem) : AbstractDate(item) {
    override val name = "Start date"
}

class EndDate(item: CustomFieldItem) : AbstractDate(item) {
    override val name = "End date"
}

class Website(item: CustomFieldItem) : Field<String> {
    override val name = "Site"
    override val value = item.value?.text as String
}

object IrrelevantField : Field<Unit> {
    override val name = "Not used"
    override val value = Unit
}

data class CustomFieldItem(
    var idCustomField: String,
    var value: Value?
)

data class Value(
    var text: String?,
    var date: String?
)
