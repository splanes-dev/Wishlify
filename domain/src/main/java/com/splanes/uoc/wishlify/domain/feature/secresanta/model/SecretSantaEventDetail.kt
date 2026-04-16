package com.splanes.uoc.wishlify.domain.feature.secresanta.model

import com.splanes.uoc.wishlify.domain.common.model.InviteLink
import com.splanes.uoc.wishlify.domain.feature.groups.model.Group
import com.splanes.uoc.wishlify.domain.feature.user.model.User
import java.util.Date

sealed class SecretSantaEventDetail(
  open val id: String,
  open val photoUrl: String?,
  open val name: String,
  open val budget: Double,
  open val isBudgetApproximate: Boolean,
  open val group: Group.Basic?,
  open val participants: List<User.Basic>,
  open val exclusions: Map<User.Basic, List<User.Basic>>,
  open val deadline: Date,
  open val inviteLink: InviteLink,
  open val createdBy: User.Basic,
  open val createdAt: Date
) {

  data class DrawPending(
    override val id: String,
    override val photoUrl: String?,
    override val name: String,
    override val budget: Double,
    override val isBudgetApproximate: Boolean,
    override val group: Group.Basic?,
    override val participants: List<User.Basic>,
    override val exclusions: Map<User.Basic, List<User.Basic>>,
    override val deadline: Date,
    override val inviteLink: InviteLink,
    override val createdBy: User.Basic,
    override val createdAt: Date
  ) : SecretSantaEventDetail(
    id = id,
    photoUrl = photoUrl,
    name = name,
    budget = budget,
    isBudgetApproximate = isBudgetApproximate,
    group = group,
    participants = participants,
    exclusions = exclusions,
    deadline = deadline,
    inviteLink = inviteLink,
    createdBy = createdBy,
    createdAt = createdAt,
  )

  data class DrawDone(
    override val id: String,
    override val photoUrl: String?,
    override val name: String,
    override val budget: Double,
    override val isBudgetApproximate: Boolean,
    override val group: Group.Basic?,
    override val participants: List<User.Basic>,
    override val exclusions: Map<User.Basic, List<User.Basic>>,
    override val deadline: Date,
    override val inviteLink: InviteLink,
    override val createdBy: User.Basic,
    override val createdAt: Date,
    val receiver: User.Basic,
    val receiverSharedWishlist: String?,
    val receiverSharedHobbies: Boolean,
    val currentUserSharedWishlist: String?,
    val receiverChatNotificationCount: Int,
    val giver: String,
    val giverChatNotificationCount: Int,
  ) : SecretSantaEventDetail(
    id = id,
    photoUrl = photoUrl,
    name = name,
    budget = budget,
    isBudgetApproximate = isBudgetApproximate,
    group = group,
    participants = participants,
    exclusions = exclusions,
    deadline = deadline,
    inviteLink = inviteLink,
    createdBy = createdBy,
    createdAt = createdAt,
  )
}
