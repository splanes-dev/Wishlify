package com.splanes.uoc.wishlify.presentation.feature.profile.feature.password.mapper

import android.content.Context
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.password.model.UserProfileUpdateNewPasswordConfirmFormPasswordError
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.password.model.UserProfileUpdateNewPasswordFormPasswordError
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.password.model.UserProfileUpdatePasswordFormError
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.password.model.UserProfileUpdatePasswordFormErrors
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.password.model.UserProfileUpdatePasswordFormPasswordError
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.password.model.UserProfileUpdatePasswordFormUiErrors

class UserProfileUpdatePasswordFormErrorUiMapper(private val context: Context) {

  fun map(errors: UserProfileUpdatePasswordFormErrors): UserProfileUpdatePasswordFormUiErrors =
    UserProfileUpdatePasswordFormUiErrors(
      currentPassword = errors.currentPassword?.let(::map),
      newPassword = errors.newPassword?.let(::map),
      newPasswordConfirm = errors.newPasswordConfirm?.let(::map),
    )

  private fun map(error: UserProfileUpdatePasswordFormError): String {
    val resources = context.resources
    return when (error) {
      UserProfileUpdateNewPasswordConfirmFormPasswordError.Blank,
      UserProfileUpdateNewPasswordFormPasswordError.Blank,
      UserProfileUpdatePasswordFormPasswordError.Blank ->
        resources.getString(R.string.input_error_mandatory)

      UserProfileUpdateNewPasswordConfirmFormPasswordError.NotMatch ->
        resources.getString(R.string.profile_update_password_confirm_do_not_match)

      UserProfileUpdateNewPasswordFormPasswordError.Weak ->
        resources.getString(R.string.input_error_weak_password)
    }
  }
}