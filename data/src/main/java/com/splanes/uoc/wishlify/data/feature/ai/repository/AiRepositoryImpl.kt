package com.splanes.uoc.wishlify.data.feature.ai.repository

import com.splanes.uoc.wishlify.data.feature.ai.datasource.InterestClassifierLocalDataSource
import com.splanes.uoc.wishlify.data.feature.ai.mapper.AiDataMapper
import com.splanes.uoc.wishlify.domain.feature.ia.repository.AiRepository
import com.splanes.uoc.wishlify.domain.feature.secretsanta.model.GiftSuggestion

class AiRepositoryImpl(
  private val dataSource: InterestClassifierLocalDataSource,
  private val mapper: AiDataMapper
): AiRepository {

  override suspend fun getGiftSuggestions(context: String): List<GiftSuggestion> {
    val result = dataSource.classify(context)
    return mapper.mapSuggestions(result)
  }
}