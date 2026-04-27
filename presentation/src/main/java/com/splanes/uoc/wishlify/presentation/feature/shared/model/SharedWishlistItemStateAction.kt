package com.splanes.uoc.wishlify.presentation.feature.shared.model

/**
 * Presentation-level actions that represent the item state transitions available in a shared
 * wishlist.
 */
enum class SharedWishlistItemStateAction {
  Purchase,
  Lock,
  RequestShare,
  Unlock,
  JoinToShareRequest,
  CancelShareRequest;
}
