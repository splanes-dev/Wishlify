package com.splanes.uoc.wishlify.presentation.feature.wishlists.mapper

import androidx.compose.ui.graphics.Color
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Category
import com.splanes.uoc.wishlify.presentation.feature.wishlists.model.CategoryUiModel

class CategoryUiMapper {

  fun map(category: Category): CategoryUiModel =
    CategoryUiModel(
      id = category.id,
      name = category.name,
      color = when (category.color) {
        Category.CategoryColor.Purple -> Color(0xFF7C4DFF)
        Category.CategoryColor.Blue -> Color(0xFF448AFF)
        Category.CategoryColor.Yellow -> Color(0xFFFFC107)
        Category.CategoryColor.Green -> Color(0xFF4CAF50)
        Category.CategoryColor.Red -> Color(0xFFF44336)
        Category.CategoryColor.Pink -> Color(0xFFE91E63)
        Category.CategoryColor.Orange -> Color(0xFFFF9800)
      }
    )
}