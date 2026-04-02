package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.creation.model

import com.splanes.uoc.wishlify.presentation.common.components.ImagePicker

data class WishlistsNewListForm(
  val image: ImagePicker.Resource?,
  val name: String,
  val categoryIndex: Int?,
  val target: String?,
  val description: String?,
) {

  enum class Input {
    Name,
    Target,
    Description,
    NewCategoryName,
  }
}