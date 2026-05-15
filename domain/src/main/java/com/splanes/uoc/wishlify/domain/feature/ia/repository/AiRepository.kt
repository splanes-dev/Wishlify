package com.splanes.uoc.wishlify.domain.feature.ia.repository

import com.splanes.uoc.wishlify.domain.feature.secretsanta.model.GiftSuggestion

interface AiRepository {
  suspend fun getGiftSuggestions(context: String): List<GiftSuggestion>
}