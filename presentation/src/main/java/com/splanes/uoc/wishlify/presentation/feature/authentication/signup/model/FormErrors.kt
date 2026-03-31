package com.splanes.uoc.wishlify.presentation.feature.authentication.signup.model

sealed interface SignUpFormError

sealed interface EmailSignUpFormError : SignUpFormError {
  data object Invalid : EmailSignUpFormError
}

sealed interface UsernameSignUpFormError : SignUpFormError {
  data object Blank : UsernameSignUpFormError
  data object Length : UsernameSignUpFormError
  data object InvalidChars : UsernameSignUpFormError
}

sealed interface PasswordSignUpFormError : SignUpFormError {
  data object Blank : PasswordSignUpFormError
  data object Weak : PasswordSignUpFormError
}