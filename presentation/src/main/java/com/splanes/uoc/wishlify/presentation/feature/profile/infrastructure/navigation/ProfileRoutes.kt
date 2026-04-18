package com.splanes.uoc.wishlify.presentation.feature.profile.infrastructure.navigation

import kotlinx.serialization.Serializable

@Serializable
data object Profile {

  @Serializable
  data object Main

  @Serializable
  data object UpdateProfile

  @Serializable
  data object UpdatePassword

  @Serializable
  data object Hobbies
}