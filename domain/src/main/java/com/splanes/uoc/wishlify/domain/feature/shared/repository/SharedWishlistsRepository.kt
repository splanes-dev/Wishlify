package com.splanes.uoc.wishlify.domain.feature.shared.repository

import com.splanes.uoc.wishlify.domain.common.model.ChatPage
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlist
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlistChatMessage
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlistItem
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlistItemUpdateStateRequest
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlistSendMessageRequest
import kotlinx.coroutines.flow.Flow

interface SharedWishlistsRepository {
  suspend fun fetchSharedWishlists(uid: String): Result<List<SharedWishlist>>
  suspend fun fetchSharedWishlist(uid: String, sharedWishlistId: String): Result<SharedWishlist>
  suspend fun unshareSharedWishlist(wishlistId: String): Result<Unit>
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

  fun subscribeToWishlistsChatMessages(
    uid: String,
    sharedWishlistId: String,
    limit: Int
  ): Flow<List<SharedWishlistChatMessage>>

  suspend fun fetchSharedWishlistMessages(
    uid: String,
    wishlistId: String,
    cursor: Long,
    limit: Int
  ): Result<ChatPage<SharedWishlistChatMessage>>

  suspend fun sendMessageToChat(uid: String, request: SharedWishlistSendMessageRequest): Result<Unit>
}