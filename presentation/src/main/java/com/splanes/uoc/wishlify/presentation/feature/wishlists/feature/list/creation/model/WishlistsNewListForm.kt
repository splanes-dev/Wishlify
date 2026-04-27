package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.creation.model

import com.splanes.uoc.wishlify.presentation.common.components.ImagePicker

/**
 * Form data collected during wishlist creation and edition.
 */
data class WishlistsNewListForm(
  val image: ImagePicker.Resource?,
  val name: String,
  val categoryIndex: Int?,
  val target: String?,
  val description: String?,
) {

  /**
   * Inputs whose validation errors can be cleared independently.
   */
  enum class Input {
    Name,
    Target,
    Description,
    NewCategoryName,
  }
}
