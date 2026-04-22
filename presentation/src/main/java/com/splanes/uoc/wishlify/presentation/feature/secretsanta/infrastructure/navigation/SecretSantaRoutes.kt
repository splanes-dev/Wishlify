package com.splanes.uoc.wishlify.presentation.feature.secretsanta.infrastructure.navigation

import kotlinx.serialization.Serializable

@Serializable
data object SecretSanta {

  @Serializable
  data object List

  @Serializable
  data object NewEvent

  @Serializable
  data class UpdateEvent(val eventId: String)

  @Serializable
  data class Detail(val eventId: String, val name: String)

  @Serializable
  data class ShareWishlist(val eventId: String)

  @Serializable
  data class Wishlist(
    val eventId: String,
    val wishlistOwnerId: String?,
    val isOwnWishlist: Boolean,
    )

  @Serializable
  data class AnonymousChat(
    val eventId: String,
    val type: String,
    val otherUid: String,
  )

  @Serializable
  data class Hobbies(
    val targetUid: String,
  )
}