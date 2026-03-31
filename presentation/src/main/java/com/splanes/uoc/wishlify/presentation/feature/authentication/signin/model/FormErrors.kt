package com.splanes.uoc.wishlify.presentation.feature.authentication.signin.model

sealed interface SignInFormError

sealed interface EmailSignInFormError : SignInFormError {
  data object Invalid : EmailSignInFormError
}
sealed interface PasswordSignInFormError : SignInFormError {
  data object Blank : PasswordSignInFormError
}