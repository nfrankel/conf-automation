package ch.frankel.conf.automation

internal const val BPMN_CONFERENCE = "conference"

internal enum class Message {
    Created, Submitted, Refused, Abandoned, Irrelevant, Backlog;

    companion object {
        operator fun invoke(name: String?): Message {
            return if (name == null) Irrelevant
            else entries.find { it.name == name } ?: Irrelevant
        }
    }
}
