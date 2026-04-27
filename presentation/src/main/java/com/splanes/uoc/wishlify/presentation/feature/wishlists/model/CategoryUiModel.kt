package com.splanes.uoc.wishlify.presentation.feature.wishlists.model

import androidx.compose.ui.graphics.Color

/**
 * Presentation model used to render a wishlist category with its resolved UI color.
 */
data class CategoryUiModel(
  val id: String,
  val name: String,
  val color: Color
)
