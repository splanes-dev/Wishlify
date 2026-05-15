package com.splanes.uoc.wishlify.data.feature.ai.model

data class AiModelMetadata(
  val sequenceLength: Int,
  val maxTokens: Int,
  val embeddingDim: Int,
  val padToken: String,
  val unkToken: String
)