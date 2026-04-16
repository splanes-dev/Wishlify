package com.splanes.uoc.wishlify.domain.feature.secresanta.repository

import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.domain.common.model.ChatPage
import com.splanes.uoc.wishlify.domain.feature.secresanta.model.CreateSecretSantaEventRequest
import com.splanes.uoc.wishlify.domain.feature.secresanta.model.SecretSantaChatMessage
import com.splanes.uoc.wishlify.domain.feature.secresanta.model.SecretSantaEvent
import com.splanes.uoc.wishlify.domain.feature.secresanta.model.SecretSantaEventDetail
import com.splanes.uoc.wishlify.domain.feature.secresanta.model.SecretSantaWishlist
import com.splanes.uoc.wishlify.domain.feature.secresanta.model.UpdateSecretSantaEventRequest
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.WishlistItem
import kotlinx.coroutines.flow.Flow

interface SecretSantaRepository {

  suspend fun fetchSecretSantaEvents(uid: String): Result<List<SecretSantaEvent>>
  suspend fun fetchSecretSantaEvent(uid: String, eventId: String): Result<SecretSantaEventDetail>
  suspend fun createSecretSantaEvent(
    uid: String,
    imageMedia: ImageMedia?,
    request: CreateSecretSantaEventRequest
  ): Result<Unit>

  suspend fun updateSecretSantaEvent(
    uid: String,
    imageMedia: ImageMedia?,
    request: UpdateSecretSantaEventRequest
  ): Result<Unit>

  suspend fun doSecretSantaDraw(
    uid: String,
    eventId: String,
    assignments: Map<String, String>,
  ): Result<Unit>

  suspend fun shareWishlistToGiver(
    uid: String,
    eventId: String,
    wishlistId: String
  ): Result<Unit>

  suspend fun unshareWishlistToGiver(
    uid: String,
    eventId: String,
  ): Result<Unit>

  suspend fun fetchSecretSantaWishlist(
    eventId: String,
    wishlistOwnerId: String,
  ): Result<SecretSantaWishlist>

  suspend fun fetchSecretSantaWishlistItems(
    eventId: String,
    wishlistOwnerId: String,
  ): Result<List<WishlistItem>>

  suspend fun subscribeToSecretSantaChatMessages(
    uid: String,
    eventId: String,
    chatId: String,
    limit: Int,
  ): Result<Flow<List<SecretSantaChatMessage>>>

  suspend fun fetchSecretSantaChatMessages(
    uid: String,
    eventId: String,
    chatId: String,
    cursor: Long,
    limit: Int
  ): Result<ChatPage<SecretSantaChatMessage>>

  suspend fun sendMessageToChat(
    uid: String,
    eventId: String,
    chatId: String,
    text: String,
  ): Result<Unit>
}