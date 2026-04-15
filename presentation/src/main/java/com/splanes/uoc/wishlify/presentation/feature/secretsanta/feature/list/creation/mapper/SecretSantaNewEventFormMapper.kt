package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.creation.mapper

import com.splanes.uoc.wishlify.domain.common.media.model.ImageMediaRequest
import com.splanes.uoc.wishlify.domain.common.model.InviteLink
import com.splanes.uoc.wishlify.domain.common.utils.newUuid
import com.splanes.uoc.wishlify.domain.feature.secresanta.model.CreateSecretSantaEventRequest
import com.splanes.uoc.wishlify.domain.feature.secresanta.model.SecretSantaEventDetail
import com.splanes.uoc.wishlify.domain.feature.secresanta.model.UpdateSecretSantaEventRequest
import com.splanes.uoc.wishlify.presentation.common.components.ImagePicker
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.creation.model.SecretSantaNewEventForm
import java.util.Date

class SecretSantaNewEventFormMapper {

  fun requestOf(
    inviteLink: InviteLink,
    form: SecretSantaNewEventForm
  ): CreateSecretSantaEventRequest =
    CreateSecretSantaEventRequest(
      id = newUuid(),
      name = form.name,
      image = when (val res = form.photo) {
        is ImagePicker.Device -> ImageMediaRequest.Device(uri = res.uri.toString())
        is ImagePicker.Url -> ImageMediaRequest.Url(url = res.url)
        else -> null
      },
      budget = form.budget,
      isBudgetApproximate = form.isBudgetApproximate,
      deadline = Date(form.deadline),
      group = form.group,
      participants = form.participants,
      exclusions = form.exclusions,
      inviteLink = inviteLink
    )

  fun requestOf(
    event: SecretSantaEventDetail,
    form: SecretSantaNewEventForm
  ): UpdateSecretSantaEventRequest =
    UpdateSecretSantaEventRequest(
      id = event.id,
      name = form.name,
      image = when (val res = form.photo) {
        is ImagePicker.Device -> ImageMediaRequest.Device(uri = res.uri.toString())
        is ImagePicker.Url -> ImageMediaRequest.Url(url = res.url)
        else -> null
      },
      budget = form.budget,
      isBudgetApproximate = form.isBudgetApproximate,
      deadline = Date(form.deadline),
      group = form.group,
      participants = form.participants,
      exclusions = form.exclusions,
      inviteLink = event.inviteLink
    )
}