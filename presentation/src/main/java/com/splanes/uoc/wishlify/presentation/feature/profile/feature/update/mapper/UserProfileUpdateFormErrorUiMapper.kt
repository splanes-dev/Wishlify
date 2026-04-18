package com.splanes.uoc.wishlify.presentation.feature.profile.feature.update.mapper

import android.content.Context
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.update.model.UserProfileUpdateFormEmailError
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.update.model.UserProfileUpdateFormError
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.update.model.UserProfileUpdateFormErrors
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.update.model.UserProfileUpdateFormNameError
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.update.model.UserProfileUpdateFormUiErrors

class UserProfileUpdateFormErrorUiMapper(private val context: Context) {

  fun map(errors: UserProfileUpdateFormErrors): UserProfileUpdateFormUiErrors =
    UserProfileUpdateFormUiErrors(
      usernameError = errors.username?.let(::map),
      emailError = errors.email?.let(::map),
    )

  private fun map(error: UserProfileUpdateFormError): String {
    val resources = context.resources
    return when (error) {
      UserProfileUpdateFormEmailError.Invalid ->
        resources.getString(R.string.input_error_invalid_format)
      UserProfileUpdateFormNameError.Blank ->
        resources.getString(R.string.input_error_mandatory)
      UserProfileUpdateFormNameError.Length ->
        resources.getString(R.string.input_error_length, 3, 20)

      UserProfileUpdateFormNameError.InvalidChars ->
        resources.getString(R.string.input_error_invalid_format)
    }
  }
}