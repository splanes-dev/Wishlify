package com.splanes.uoc.wishlify.presentation.feature.groups.infrastructure.navigation

import kotlinx.serialization.Serializable

@Serializable
data object Groups {

  @Serializable
  data object List

  @Serializable
  data object NewGroup

  @Serializable
  data object SearchUsers
}