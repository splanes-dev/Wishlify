package com.splanes.uoc.wishlify.presentation.feature.authentication.signin.mapper

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ErrorOutline
import com.splanes.uoc.wishlify.domain.feature.authentication.error.SignInError
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiMapper
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiModel

class SignInErrorMapper(context: Context) : ErrorUiMapper(context) {

  override fun map(error: Throwable): ErrorUiModel =
    if (error is SignInError) {
      when (error) {
        is SignInError.InvalidCredentials ,
        is SignInError.InvalidEmail ->
          errorOf(
            icon = Icons.Rounded.ErrorOutline,
            title = R.string.error_dialog_title_oops,
            description = R.string.error_dialog_signin_invalid_credentials_description,
          )

        else ->
          super.map(error)
      }
    } else {
      super.map(error)
    }

}