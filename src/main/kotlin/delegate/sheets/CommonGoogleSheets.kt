package ch.frankel.conf.automation.delegate.sheets

import ch.frankel.conf.automation.action.Conference

internal class Result(values: Collection<Any>) {
    private val majorDimension: String
    private val range: String
    private val rows: List<ResultRow>

    init {
        val wrapper: List<Any> = ArrayList(values)
        majorDimension = wrapper[0] as String
        range = wrapper[1] as String
        rows = (wrapper[2] as List<List<Any>>)
            .filter { it.isNotEmpty() }
            .mapIndexed { index, elements -> ResultRow(index + 3, elements) }
    }

    fun findRowIndices(speaker: String, conference: Conference) = rows
        .filter { it.isMatch(speaker, conference.name, "Submitted") }
        .map { it.index }

    override fun toString() = "Sheet(dimension='$majorDimension', range='$range', rows=$rows)"
}

internal class ResultRow(val index: Int, values: List<Any>) {

    val name = values[0]
    val speaker = values[1]
    @Suppress("unused")
    val talk = values[2]
    @Suppress("unused")
    val country = values[3]
    @Suppress("unused")
    val startDate = values[4]
    @Suppress("unused")
    val endDate = values[5]
    @Suppress("unused")
    val related = values[6]
    val status = values[7]

    fun isMatch(speaker: String, conference: String, status: String) =
        this.name == conference && this.speaker == speaker && this.status == status

    override fun toString() = "[$index]->(name=$name, speaker=$speaker, status=$status)"
}
