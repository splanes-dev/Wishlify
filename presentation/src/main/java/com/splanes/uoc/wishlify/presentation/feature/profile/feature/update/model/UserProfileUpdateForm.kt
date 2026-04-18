package com.splanes.uoc.wishlify.presentation.feature.profile.feature.update.model

import com.splanes.uoc.wishlify.presentation.common.components.ImagePicker

data class UserProfileUpdateForm(
  val photo: ImagePicker.Resource? = null,
  val username: String = "",
  val email: String = "",
  val isSocialAccount: Boolean = false
) {
  enum class Input {
    Username,
    Email,
  }
}

data class UserProfileUpdateFormUiErrors(
  val usernameError: String?,
  val emailError: String?
)