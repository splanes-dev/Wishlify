package com.splanes.uoc.wishlify.domain.feature.wishlists.model

/** Custom category used to classify wishlists. */
data class Category(
  val id: String,
  val name: String,
  val color: CategoryColor
) {
  /** Available colors for wishlist categories. */
  enum class CategoryColor {
    Purple,
    Blue,
    Yellow,
    Green,
    Red,
    Pink,
    Orange;

    companion object {
      /** Resolves a category color from its lowercase persisted name. */
      fun from(name: String) =
        entries.first { it.name.lowercase() == name }
    }
  }
}
