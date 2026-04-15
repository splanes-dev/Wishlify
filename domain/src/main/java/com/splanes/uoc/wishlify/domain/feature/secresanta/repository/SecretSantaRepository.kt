package com.splanes.uoc.wishlify.domain.feature.secresanta.repository

import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.domain.feature.secresanta.model.CreateSecretSantaEventRequest
import com.splanes.uoc.wishlify.domain.feature.secresanta.model.SecretSantaEvent
import com.splanes.uoc.wishlify.domain.feature.secresanta.model.SecretSantaEventDetail
import com.splanes.uoc.wishlify.domain.feature.secresanta.model.UpdateSecretSantaEventRequest

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
}