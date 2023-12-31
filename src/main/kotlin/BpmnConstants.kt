package ch.frankel.conf.automation

internal const val BPMN_CONFERENCE = "conference"

enum class Message {
    Created, Submitted, Accepted, Refused, Abandoned, Irrelevant, Backlog, Published;

    companion object {
        operator fun invoke(name: String?): Message {
            return if (name == null) Irrelevant
            else entries.find { it.name == name } ?: Irrelevant
        }
    }
}
