package ch.frankel.conf.automation.action

internal const val BPMN_TRANSITION = "transition"

internal enum class Status {
    Backlog,
    Submitted,
    Accepted,
    Refused,
    Abandoned,
}

sealed class CardChange {
    companion object {
        private val statusToTransitions = mapOf(
            ("Backlog" to "Submitted") to BacklogToSubmitted,
            ("Submitted" to "Accepted") to SubmittedToAccepted,
            ("Submitted" to "Refused") to SubmittedToRefused,
            ("Backlog" to "Abandoned") to BacklogToAbandoned,
        )
        private val stringToTransitions = mapOf(
            "BacklogToSubmitted" to BacklogToSubmitted,
            "BacklogToAbandoned" to BacklogToAbandoned,
            "SubmittedToAccepted" to SubmittedToAccepted,
            "SubmittedToRefused" to SubmittedToRefused,
        )

        fun from(transition: String) = stringToTransitions.getOrDefault(transition, IrrelevantChange)
        fun from(transition: Pair<String?, String?>) = statusToTransitions.getOrDefault(transition, IrrelevantChange)
    }
}

sealed class StatusTransition(private val start: Status, internal val end: Status) : CardChange() {
    override fun toString() = "${start}To${end}"
}

data object BacklogToSubmitted : StatusTransition(Status.Backlog, Status.Submitted)
data object BacklogToAbandoned : StatusTransition(Status.Backlog, Status.Abandoned)
data object SubmittedToAccepted : StatusTransition(Status.Submitted, Status.Accepted)
data object SubmittedToRefused : StatusTransition(Status.Submitted, Status.Refused)
data object IrrelevantChange : CardChange()
