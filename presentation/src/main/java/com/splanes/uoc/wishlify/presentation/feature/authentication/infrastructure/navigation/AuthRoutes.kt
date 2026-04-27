package com.splanes.uoc.wishlify.presentation.feature.authentication.infrastructure.navigation

import kotlinx.serialization.Serializable

/** Root route of the authentication navigation graph. */
@Serializable
data object Auth

/** Route of the sign-in screen. */
@Serializable
data object SignIn

/** Route of the sign-up screen. */
@Serializable
data object SignUp
