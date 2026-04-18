package com.splanes.uoc.wishlify.presentation.feature.profile.feature.password.mapper

import com.splanes.uoc.wishlify.domain.feature.user.model.UpdatePasswordRequest
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.password.model.UserProfileUpdatePasswordForm

class UserProfileUpdatePasswordFormMapper {

  fun requestOf(form: UserProfileUpdatePasswordForm): UpdatePasswordRequest =
    UpdatePasswordRequest(
      current = form.currentPassword,
      new = form.newPassword
    )
}