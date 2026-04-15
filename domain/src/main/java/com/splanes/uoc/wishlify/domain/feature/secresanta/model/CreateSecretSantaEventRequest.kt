package com.splanes.uoc.wishlify.domain.feature.secresanta.model

import com.splanes.uoc.wishlify.domain.common.media.model.ImageMediaRequest
import com.splanes.uoc.wishlify.domain.common.model.InviteLink
import com.splanes.uoc.wishlify.domain.feature.groups.model.Group
import com.splanes.uoc.wishlify.domain.feature.user.model.User
import java.util.Date

data class CreateSecretSantaEventRequest(
  val id: String,
  val name: String,
  val image: ImageMediaRequest?,
  val budget: Double,
  val isBudgetApproximate: Boolean,
  val deadline: Date,
  val group: Group.Basic?,
  val participants: List<User.Basic>,
  val exclusions: List<Pair<User.Basic, User.Basic>>,
  val inviteLink: InviteLink,
)
