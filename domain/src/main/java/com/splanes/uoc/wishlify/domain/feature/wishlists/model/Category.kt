package com.splanes.uoc.wishlify.domain.feature.wishlists.model

data class Category(
  val id: String,
  val name: String,
  val color: CategoryColor
) {
  enum class CategoryColor {
    Purple,
    Blue,
    Yellow,
    Green,
    Red,
    Pink,
    Orange;

    companion object {
      fun from(name: String) =
        entries.first { it.name.lowercase() == name }
    }
  }
}
