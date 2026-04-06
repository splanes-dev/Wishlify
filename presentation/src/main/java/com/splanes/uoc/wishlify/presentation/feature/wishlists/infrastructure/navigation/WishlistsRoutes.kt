package com.splanes.uoc.wishlify.presentation.feature.wishlists.infrastructure.navigation

import kotlinx.serialization.Serializable

@Serializable
data object Wishlists {

  @Serializable
  data object List

  @Serializable
  data object Categories

  @Serializable
  data class NewList(val isOwn: Boolean)

  @Serializable
  data class Detail(val id: String, val name: String)

  @Serializable
  data class NewItem(val wishlistId: String, val link: String?)

  @Serializable
  data class EditItem(val wishlistId: String, val itemId: String)
}