package com.splanes.uoc.wishlify.presentation.feature.profile.feature.update.model

import com.splanes.uoc.wishlify.presentation.common.components.ImagePicker

/**
 * Form data collected during profile update.
 */
data class UserProfileUpdateForm(
  val photo: ImagePicker.Resource? = null,
  val username: String = "",
  val email: String = "",
  val isSocialAccount: Boolean = false
) {
  /**
   * Inputs whose validation errors can be cleared independently.
   */
  enum class Input {
    Username,
    Email,
  }
}

/**
 * UI-ready validation messages for the profile update form.
 */
data class UserProfileUpdateFormUiErrors(
  val usernameError: String?,
  val emailError: String?
)
