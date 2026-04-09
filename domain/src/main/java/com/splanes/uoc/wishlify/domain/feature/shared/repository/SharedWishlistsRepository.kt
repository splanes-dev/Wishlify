package com.splanes.uoc.wishlify.domain.feature.shared.repository

import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlist
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlistItem
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlistItemUpdateStateRequest

interface SharedWishlistsRepository {
  suspend fun fetchSharedWishlists(uid: String): Result<List<SharedWishlist>>
  suspend fun fetchSharedWishlist(uid: String, sharedWishlistId: String): Result<SharedWishlist>
  suspend fun fetchSharedWishlistItems(
    uid: String,
    sharedWishlistId: String
  ): Result<List<SharedWishlistItem>>

  suspend fun fetchSharedWishlistItem(
    uid: String,
    sharedWishlistId: String,
    sharedWishlistItemId: String,
  ): Result<SharedWishlistItem>

  suspend fun updateSharedWishlistItemState(
    uid: String,
    request: SharedWishlistItemUpdateStateRequest
  ): Result<Unit>
}