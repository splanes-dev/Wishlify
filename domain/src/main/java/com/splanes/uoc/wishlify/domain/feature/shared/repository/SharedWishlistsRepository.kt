package com.splanes.uoc.wishlify.domain.feature.shared.repository

import com.splanes.uoc.wishlify.domain.common.model.ChatPage
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlist
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlistChatMessage
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlistItem
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlistItemUpdateStateRequest
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlistSendMessageRequest
import kotlinx.coroutines.flow.Flow

/** Repository contract for shared wishlists, their items and their chat. */
interface SharedWishlistsRepository {
  /** Retrieves the shared wishlists visible to the given user. */
  suspend fun fetchSharedWishlists(uid: String): Result<List<SharedWishlist>>
  /** Retrieves a single shared wishlist for the given user. */
  suspend fun fetchSharedWishlist(uid: String, sharedWishlistId: String): Result<SharedWishlist>
  /** Converts a shared wishlist back into a private wishlist flow. */
  suspend fun unshareSharedWishlist(wishlistId: String): Result<Unit>
  /** Retrieves the items of a shared wishlist for the given user. */
  suspend fun fetchSharedWishlistItems(
    uid: String,
    sharedWishlistId: String
  ): Result<List<SharedWishlistItem>>

  /** Subscribes to real-time updates of shared wishlist items. */
  suspend fun subscribeToSharedWishlistItems(
    uid: String,
    sharedWishlistId: String
  ): Flow<List<SharedWishlistItem>>

  /** Retrieves a single shared wishlist item for the given user. */
  suspend fun fetchSharedWishlistItem(
    uid: String,
    sharedWishlistId: String,
    sharedWishlistItemId: String,
  ): Result<SharedWishlistItem>

  /** Persists a collaborative state transition for a shared wishlist item. */
  suspend fun updateSharedWishlistItemState(
    uid: String,
    request: SharedWishlistItemUpdateStateRequest
  ): Result<Unit>

  /** Subscribes to real-time chat messages of a shared wishlist. */
  fun subscribeToWishlistsChatMessages(
    uid: String,
    sharedWishlistId: String,
    limit: Int
  ): Flow<List<SharedWishlistChatMessage>>

  /** Fetches a paginated batch of older shared wishlist chat messages. */
  suspend fun fetchSharedWishlistMessages(
    uid: String,
    wishlistId: String,
    cursor: Long,
    limit: Int
  ): Result<ChatPage<SharedWishlistChatMessage>>

  /** Sends a chat message to a shared wishlist conversation. */
  suspend fun sendMessageToChat(uid: String, request: SharedWishlistSendMessageRequest): Result<Unit>

  /** Adds the current user to a shared wishlist using an invitation token. */
  suspend fun addParticipantByToken(token: String): Result<Unit>
}
