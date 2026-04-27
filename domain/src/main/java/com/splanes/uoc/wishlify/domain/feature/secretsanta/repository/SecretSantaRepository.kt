package com.splanes.uoc.wishlify.domain.feature.secretsanta.repository

import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.domain.common.model.ChatPage
import com.splanes.uoc.wishlify.domain.feature.secretsanta.model.CreateSecretSantaEventRequest
import com.splanes.uoc.wishlify.domain.feature.secretsanta.model.SecretSantaChatMessage
import com.splanes.uoc.wishlify.domain.feature.secretsanta.model.SecretSantaEvent
import com.splanes.uoc.wishlify.domain.feature.secretsanta.model.SecretSantaEventDetail
import com.splanes.uoc.wishlify.domain.feature.secretsanta.model.SecretSantaWishlist
import com.splanes.uoc.wishlify.domain.feature.secretsanta.model.UpdateSecretSantaEventRequest
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.WishlistItem
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for Secret Santa events, assignments, shared wishlists and chats.
 */
interface SecretSantaRepository {

  /** Retrieves the Secret Santa events visible to the given user. */
  suspend fun fetchSecretSantaEvents(uid: String): Result<List<SecretSantaEvent>>
  /** Retrieves the detailed view of a single Secret Santa event for the given user. */
  suspend fun fetchSecretSantaEvent(uid: String, eventId: String): Result<SecretSantaEventDetail>
  /** Creates a new Secret Santa event. */
  suspend fun createSecretSantaEvent(
    uid: String,
    imageMedia: ImageMedia?,
    request: CreateSecretSantaEventRequest
  ): Result<Unit>

  /** Persists the updated state of an existing Secret Santa event. */
  suspend fun updateSecretSantaEvent(
    uid: String,
    imageMedia: ImageMedia?,
    request: UpdateSecretSantaEventRequest
  ): Result<Unit>

  /** Stores the final assignments produced by the draw for the given event. */
  suspend fun doSecretSantaDraw(
    uid: String,
    eventId: String,
    assignments: Map<String, String>,
  ): Result<Unit>

  /** Shares one of the current user's wishlists with their assigned giver. */
  suspend fun shareWishlistToGiver(
    uid: String,
    eventId: String,
    wishlistId: String
  ): Result<Unit>

  /** Removes the current user's previously shared wishlist from the event. */
  suspend fun unshareWishlistToGiver(
    uid: String,
    eventId: String,
  ): Result<Unit>

  /** Retrieves the wishlist shared by a participant within a Secret Santa event. */
  suspend fun fetchSecretSantaWishlist(
    eventId: String,
    wishlistOwnerId: String,
  ): Result<SecretSantaWishlist>

  /** Retrieves the items of a wishlist shared within a Secret Santa event. */
  suspend fun fetchSecretSantaWishlistItems(
    eventId: String,
    wishlistOwnerId: String,
  ): Result<List<WishlistItem>>

  /** Subscribes to real-time messages of a Secret Santa chat. */
  suspend fun subscribeToSecretSantaChatMessages(
    uid: String,
    eventId: String,
    chatId: String,
    limit: Int,
  ): Result<Flow<List<SecretSantaChatMessage>>>

  /** Fetches a paginated batch of messages from a Secret Santa chat. */
  suspend fun fetchSecretSantaChatMessages(
    uid: String,
    eventId: String,
    chatId: String,
    cursor: Long,
    limit: Int
  ): Result<ChatPage<SecretSantaChatMessage>>

  /** Sends a message to a Secret Santa chat. */
  suspend fun sendMessageToChat(
    uid: String,
    eventId: String,
    chatId: String,
    text: String,
  ): Result<Unit>

  /** Adds the current user to an event using an invitation token. */
  suspend fun addEventParticipantByToken(token: String): Result<Unit>
}
