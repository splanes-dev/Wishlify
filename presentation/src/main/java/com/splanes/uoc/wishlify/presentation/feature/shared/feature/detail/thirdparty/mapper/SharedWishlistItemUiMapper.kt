package com.splanes.uoc.wishlify.presentation.feature.shared.feature.detail.thirdparty.mapper

import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlist
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlistItem
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlistItemStateRequest
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlistItemUpdateStateRequest
import com.splanes.uoc.wishlify.presentation.feature.shared.feature.detail.thirdparty.model.SharedWishlistItemAction

class SharedWishlistItemUiMapper {

  fun updateRequestOf(
    wishlist: SharedWishlist,
    item: SharedWishlistItem,
    action: SharedWishlistItemAction.UpdateState
  ): SharedWishlistItemUpdateStateRequest {
    val newState = when (action) {
      SharedWishlistItemAction.CancelShareRequest -> SharedWishlistItemStateRequest.CancelShareRequest
      SharedWishlistItemAction.JoinToShareRequest -> SharedWishlistItemStateRequest.JoinToShareRequest
      SharedWishlistItemAction.Lock -> SharedWishlistItemStateRequest.Lock
      SharedWishlistItemAction.Purchase -> SharedWishlistItemStateRequest.Purchase
      is SharedWishlistItemAction.ShareRequest -> SharedWishlistItemStateRequest.ShareRequest(action.numOfParticipants)
      SharedWishlistItemAction.Unlock -> SharedWishlistItemStateRequest.Unlock
    }

    return SharedWishlistItemUpdateStateRequest(wishlist, item, newState)
  }
}