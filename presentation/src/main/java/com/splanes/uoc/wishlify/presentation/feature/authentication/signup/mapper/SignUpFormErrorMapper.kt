package com.splanes.uoc.wishlify.presentation.feature.authentication.signup.mapper

import android.content.Context
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.feature.authentication.signup.model.EmailSignUpFormError
import com.splanes.uoc.wishlify.presentation.feature.authentication.signup.model.PasswordSignUpFormError
import com.splanes.uoc.wishlify.presentation.feature.authentication.signup.model.SignUpFormError
import com.splanes.uoc.wishlify.presentation.feature.authentication.signup.model.UsernameSignUpFormError

class SignUpFormErrorMapper(private val context: Context) {

  fun map(error: SignUpFormError): String {
    val resources = context.resources
    return when (error) {
      EmailSignUpFormError.Invalid ->
        resources.getString(R.string.input_error_invalid_format)
      PasswordSignUpFormError.Blank ->
        resources.getString(R.string.input_error_mandatory)
      PasswordSignUpFormError.Weak ->
        resources.getString(R.string.input_error_weak_password)
      UsernameSignUpFormError.Blank ->
        resources.getString(R.string.input_error_mandatory)
      UsernameSignUpFormError.InvalidChars ->
        resources.getString(R.string.input_error_invalid_format)
      UsernameSignUpFormError.Length ->
        resources.getString(R.string.input_error_length, 3, 20)
    }
  }
}