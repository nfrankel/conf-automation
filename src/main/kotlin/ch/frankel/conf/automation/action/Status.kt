package ch.frankel.conf.automation.action

internal const val BPMN_STATUS = "status"

internal enum class Status {
    Submitted,
    Accepted,
    Refused,
    Abandoned,
    Irrelevant
}
