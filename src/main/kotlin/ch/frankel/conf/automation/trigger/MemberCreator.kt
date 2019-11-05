package ch.frankel.conf.automation.trigger

import java.io.Serializable

data class MemberCreator(
    val activityBlocked: Boolean,
    val avatarHash: String,
    val avatarUrl: String,
    val fullName: String,
    val id: String,
    val idMemberReferrer: String?,
    val initials: String,
    val nonPublicAvailable: Boolean,
    val username: String
): Serializable