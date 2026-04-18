package com.splanes.uoc.wishlify.presentation.feature.profile.feature.update.mapper

import com.splanes.uoc.wishlify.domain.common.media.model.ImageMediaRequest
import com.splanes.uoc.wishlify.domain.feature.user.model.UpdateProfileRequest
import com.splanes.uoc.wishlify.domain.feature.user.model.User
import com.splanes.uoc.wishlify.presentation.common.components.ImagePicker
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.update.model.UserProfileUpdateForm

class UserProfileUpdateFormMapper {

  fun map(user: User.BasicProfile, form: UserProfileUpdateForm): UpdateProfileRequest =
    UpdateProfileRequest.BasicInfo(
      user = user,
      media = when (val media = form.photo) {
        is ImagePicker.Device -> ImageMediaRequest.Device(media.uri.toString())
        is ImagePicker.Url -> ImageMediaRequest.Url(media.url)
        else -> null
      },
      username = form.username,
      email = form.email
    )
}