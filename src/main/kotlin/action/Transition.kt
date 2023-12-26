package ch.frankel.conf.automation.action

internal const val BPMN_TRANSITION = "transition"

internal enum class Status {
    Backlog,
    Submitted,
    Accepted,
    Refused,
    Abandoned,
}

internal data class Transition(val start: Status?, val end: Status?) {

    companion object {
        val BacklogToSubmitted = Transition(Status.Backlog, Status.Submitted)
        val BacklogToAbandoned = Transition(Status.Backlog, Status.Abandoned)
        val SubmittedToAccepted = Transition(Status.Submitted, Status.Accepted)
        val SubmittedToRefused = Transition(Status.Submitted, Status.Refused)
        val Irrelevant = Transition(null, null)

        fun fromString(transition: String): Transition {
            return when (transition) {
                "BacklogToSubmitted" -> BacklogToSubmitted
                "BacklogToAbandoned" -> BacklogToAbandoned
                "SubmittedToAccepted" -> SubmittedToAccepted
                "SubmittedToRefused" -> SubmittedToRefused
                else -> Irrelevant
            }
        }
    }

    override fun toString() = "${start}To${end}"
}

internal val transitionStatusMatrix = mapOf(
    ("Backlog" to "Submitted") to Transition.BacklogToSubmitted,
    ("Submitted" to "Accepted") to Transition.SubmittedToAccepted,
    ("Submitted" to "Refused") to Transition.SubmittedToRefused,
    ("Backlog" to "Abandoned") to Transition.BacklogToAbandoned,
)
