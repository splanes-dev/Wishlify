package com.splanes.uoc.wishlify.presentation.feature.secretsanta.infrastructure.navigation

import kotlinx.serialization.Serializable

/**
 * Root navigation route for the Secret Santa feature and its nested destinations.
 */
@Serializable
data object SecretSanta {

  /**
   * Secret Santa events list.
   */
  @Serializable
  data object List

  /**
   * Secret Santa event creation flow.
   */
  @Serializable
  data object NewEvent

  /**
   * Secret Santa event edition flow.
   */
  @Serializable
  data class UpdateEvent(val eventId: String)

  /**
   * Secret Santa event detail.
   */
  @Serializable
  data class Detail(val eventId: String, val name: String)

  /**
   * Flow used to share a wishlist with the current event.
   */
  @Serializable
  data class ShareWishlist(val eventId: String)

  /**
   * Secret Santa wishlist detail for either the current user or the assigned receiver.
   */
  @Serializable
  data class Wishlist(
    val eventId: String,
    val wishlistOwnerId: String?,
    val isOwnWishlist: Boolean,
    )

  /**
   * Anonymous chat associated with the event and the current participant role.
   */
  @Serializable
  data class AnonymousChat(
    val eventId: String,
    val type: String,
    val otherUid: String,
  )

  /**
   * Hobbies screen for the assigned receiver.
   */
  @Serializable
  data class Hobbies(
    val targetUid: String,
  )
}
