package com.splanes.uoc.wishlify.presentation.feature.wishlists.infrastructure.navigation

import kotlinx.serialization.Serializable

@Serializable
data object Wishlists {

  @Serializable
  data object List

  @Serializable
  data class NewList(val isOwn: Boolean)
}