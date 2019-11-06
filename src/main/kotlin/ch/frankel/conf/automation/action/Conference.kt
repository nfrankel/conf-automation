package ch.frankel.conf.automation.action

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor

class Conference(title: String, fields: List<Field<out Any>>) {

    val startDate: LocalDate = LocalDate.from((fields.find { it is StartDate } as StartDate).value)
    val endDate: LocalDate = LocalDate.from((fields.find { it is EndDate } as EndDate).value)
    val website = (fields.find { it is Website } as Website).value
    val name: String
    val location: String

    init {
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
    }

    override fun toString() = "Conference(summary=$name,location=$location,website=\"$website\",startDate=$startDate,endDate=$endDate"
}


abstract class Field<T> {
    abstract val name: String
    abstract val value: T
}

abstract class AbstractDate(item: CustomFieldItem) : Field<TemporalAccessor>() {
    override val value: TemporalAccessor = DateTimeFormatter.ISO_ZONED_DATE_TIME.parse(item.value?.date)
}

class StartDate(item: CustomFieldItem) : AbstractDate(item) {
    override val name = "Start date"
}

class EndDate(item: CustomFieldItem) : AbstractDate(item) {
    override val name = "End date"
}

class Website(item: CustomFieldItem) : Field<String>() {
    override val name = "Site"
    override val value = item.value?.text as String
}

object IrrelevantField : Field<Unit>() {
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