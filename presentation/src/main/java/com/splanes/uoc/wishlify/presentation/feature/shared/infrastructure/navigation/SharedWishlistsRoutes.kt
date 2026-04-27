package com.splanes.uoc.wishlify.presentation.feature.shared.infrastructure.navigation

import kotlinx.serialization.Serializable

/**
 * Root navigation route for shared wishlists and its nested destinations.
 */
@Serializable
data object SharedWishlists {

  /**
   * Shared wishlists list.
   */
  @Serializable
  data object List

  /**
   * Detail flow for a shared wishlist opened by a third-party participant.
   */
  @Serializable
  data class ThirdPartyDetail(
    val sharedWishlistId: String,
    val sharedWishlistName: String,
    val target: String
  )

  /**
   * Chat flow associated with a third-party shared wishlist.
   */
  @Serializable
  data class ThirdPartyChat(
    val sharedWishlistId: String,
    val sharedWishlistName: String,
    val target: String
  )
}
