package com.splanes.uoc.wishlify.domain.feature.ia.repository

import com.splanes.uoc.wishlify.domain.feature.secretsanta.model.GiftSuggestion
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.SuggestedTag

interface AiRepository {
  suspend fun getWishlistItemTags(context: String): List<SuggestedTag>
  suspend fun getGiftSuggestions(context: String): List<GiftSuggestion>
}