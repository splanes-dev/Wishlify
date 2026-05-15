package com.splanes.uoc.wishlify.domain.feature.secretsanta.model

data class GiftSuggestionsAiContext(
  val aiContext: String,
  val budget: Float?,
  val deadline: Long?,
  val sources: List<Source>
) {

  enum class Source {
    Hobbies,
    Wishlists,
    WishlistItems
  }
}
