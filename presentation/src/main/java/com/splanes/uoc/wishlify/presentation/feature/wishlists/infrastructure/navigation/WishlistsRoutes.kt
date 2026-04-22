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
  data class EditList(val wishlistId: String)

  @Serializable
  data class ShareList(val wishlistId: String)

  @Serializable
  data class Detail(val id: String, val name: String)

  @Serializable
  data class DetailShared(val id: String, val name: String, val target: String?)

  @Serializable
  data class NewItem(
    val wishlistId: String,
    val link: String?,
    val imageUrl: String? = null
  )

  @Serializable
  data class EditItem(val wishlistId: String, val itemId: String)
}