package com.splanes.uoc.wishlify.domain.feature.shared.model

/** Input required to update the collaborative state of a shared wishlist item. */
data class SharedWishlistItemUpdateStateRequest(
  val sharedWishlist: SharedWishlist,
  val item: SharedWishlistItem,
  val newStateRequest: SharedWishlistItemStateRequest
)

/** Requested transition to apply to a shared wishlist item. */
sealed interface SharedWishlistItemStateRequest {
  data object Purchase : SharedWishlistItemStateRequest
  data object Lock : SharedWishlistItemStateRequest
  data class ShareRequest(val numOfParticipants: Int) : SharedWishlistItemStateRequest
  data object Unlock : SharedWishlistItemStateRequest
  data object JoinToShareRequest : SharedWishlistItemStateRequest
  data object CancelShareRequest : SharedWishlistItemStateRequest
}
