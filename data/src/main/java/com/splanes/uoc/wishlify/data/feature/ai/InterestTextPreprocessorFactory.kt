package com.splanes.uoc.wishlify.data.feature.ai

import com.splanes.uoc.wishlify.data.feature.ai.util.AiAssetsLoader

class InterestTextPreprocessorFactory(
  private val loader: AiAssetsLoader,
) {

  fun create(): InterestTextPreprocessor {
    val vocabulary = loader.loadVocabulary()
    val metadata = loader.loadMetadata()

    return InterestTextPreprocessor(
      vocabulary = vocabulary,
      sequenceLength = metadata.sequenceLength,
      unknownToken = metadata.unkToken,
      paddingToken = metadata.padToken,
    )
  }
}