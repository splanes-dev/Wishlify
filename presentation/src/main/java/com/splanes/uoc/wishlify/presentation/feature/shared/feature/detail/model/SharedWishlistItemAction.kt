package com.splanes.uoc.wishlify.presentation.feature.shared.feature.detail.model

/**
 * Actions that can be triggered for an item inside a third-party shared wishlist.
 */
sealed interface SharedWishlistItemAction{
  data object Open : SharedWishlistItemAction
  data object OpenLink : SharedWishlistItemAction

  /**
   * Actions that mutate the collaborative state of the selected item.
   */
  sealed interface UpdateState : SharedWishlistItemAction

  data object Purchase : UpdateState
  data object Lock : UpdateState
  data class ShareRequest(val numOfParticipants: Int) : UpdateState
  data object Unlock : UpdateState
  data object JoinToShareRequest : UpdateState
  data object CancelShareRequest : UpdateState
}
