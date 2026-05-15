package com.splanes.uoc.wishlify.data.feature.ai

class InterestTextPreprocessor(
  private val vocabulary: Map<String, Int>,
  private val sequenceLength: Int,
  unknownToken: String = "<UNK>",
  paddingToken: String = "<PAD>",
) {
  private val unknownTokenId: Int = vocabulary[unknownToken] ?: 1
  private val paddingTokenId: Int = vocabulary[paddingToken] ?: 0

  fun preprocess(text: String): IntArray {
    val tokens = tokenize(text)
    val tokenIds = tokens
      .map { token -> vocabulary[token] ?: unknownTokenId }
      .take(sequenceLength)
      .toMutableList()

    while (tokenIds.size < sequenceLength) {
      tokenIds.add(paddingTokenId)
    }

    return tokenIds.toIntArray()
  }

  private fun tokenize(text: String): List<String> {
    return normalize(text)
      .split(" ")
      .filter { it.isNotBlank() }
  }

  private fun normalize(text: String): String {
    return text
      .lowercase()
      .replace(Regex("[^\\w\\sàèéíïòóúüçñ·&]"), " ")
      .replace(Regex("\\s+"), " ")
      .trim()
  }
}