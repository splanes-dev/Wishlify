package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.creation

import com.splanes.uoc.wishlify.domain.common.model.InviteLink
import com.splanes.uoc.wishlify.domain.feature.groups.model.Group
import com.splanes.uoc.wishlify.domain.feature.user.model.User
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiModel
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.creation.model.SecretSantaNewEventForm
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.creation.model.SecretSantaNewEventFormUiErrors
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.creation.model.SecretSantaNewEventStep

data class SecretSantaNewEventUiState(
  val step: SecretSantaNewEventStep,
  val form: SecretSantaNewEventForm,
  val formErrors: SecretSantaNewEventFormUiErrors,
  val groups: List<Group.Basic>,
  val allParticipants: List<User.Basic>,
  val inviteLink: InviteLink,
  val isLoading: Boolean,
  val error: ErrorUiModel?,
)

sealed interface SecretSantaNewEventUiSideEffect {
  data object EventCreated : SecretSantaNewEventUiSideEffect
}