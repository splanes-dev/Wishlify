package com.splanes.uoc.wishlify.presentation.feature.authentication.signin.mapper

import android.content.Context
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.feature.authentication.signin.model.EmailSignInFormError
import com.splanes.uoc.wishlify.presentation.feature.authentication.signin.model.PasswordSignInFormError
import com.splanes.uoc.wishlify.presentation.feature.authentication.signin.model.SignInFormError

class SignInFormErrorMapper(private val context: Context) {

  fun map(error: SignInFormError): String {
    val resources = context.resources
    return when (error) {
      EmailSignInFormError.Invalid ->
        resources.getString(R.string.input_error_invalid_format)
      PasswordSignInFormError.Blank ->
        resources.getString(R.string.input_error_mandatory)
    }
  }
}