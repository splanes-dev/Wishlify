package com.splanes.uoc.wishlify.presentation.feature.authentication.signup.mapper

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.NoAccounts
import androidx.compose.material.icons.rounded.Warning
import com.splanes.uoc.wishlify.domain.feature.authentication.error.SignUpError
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiMapper
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiModel

class SignUpErrorMapper(context: Context) : ErrorUiMapper(context) {

  override fun map(error: Throwable): ErrorUiModel =
    if (error is SignUpError) {
      when (error) {
        is SignUpError.GoogleSignUpFailed ->
          errorOf(
            icon = Icons.Rounded.ErrorOutline,
            title = R.string.error_dialog_title_oops,
            description = R.string.error_dialog_signup_social_description,
          )

        is SignUpError.Unknown ->
          super.map(error)

        is SignUpError.UserCollision ->
          errorOf(
            icon = Icons.Rounded.NoAccounts,
            title = R.string.error_dialog_title_oops,
            description = R.string.error_dialog_signup_user_collision_description,
          )

        is SignUpError.WeakPassword ->
          errorOf(
            icon = Icons.Rounded.Warning,
            title = R.string.error_dialog_title_warning,
            description = R.string.error_dialog_signup_weak_password_description,
          )
      }
    } else {
      super.map(error)
    }
}