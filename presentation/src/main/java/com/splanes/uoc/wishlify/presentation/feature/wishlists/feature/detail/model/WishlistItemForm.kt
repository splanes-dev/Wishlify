package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.model

import com.splanes.uoc.wishlify.domain.feature.wishlists.model.WishlistItem
import com.splanes.uoc.wishlify.presentation.common.components.ImagePicker

data class WishlistItemForm(
  val photo: ImagePicker.Resource? = null,
  val name: String = "",
  val description: String = "",
  val store: String = "",
  val unitPrice: Float = 0f,
  val amount: Int = 1,
  val priority: WishlistItem.Priority = WishlistItem.Priority.Standard,
  val link: String = "",
  val tags: String = "",
) {

  enum class Input {
    Name,
    Store,
    Price,
    Amount,
    Priority,
    Link,
    Tags,
    Description
  }
}

data class WishlistItemFormErrors(
  val name: NameWishlistItemFormError? = null,
  val description: DescriptionWishlistItemFormError? = null,
  val store: StoreWishlistItemFormError? = null,
  val unitPrice: PriceWishlistItemFormError? = null,
  val amount: AmountWishlistItemFormError? = null,
  val link: LinkWishlistItemFormError? = null,
  val tags: TagsWishlistItemFormError? = null,
)

data class WishlistItemUiFormErrors(
  val name: String? = null,
  val description: String? = null,
  val store: String? = null,
  val unitPrice: String? = null,
  val amount: String? = null,
  val link: String? = null,
  val tags: String? = null,
)