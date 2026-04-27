package com.splanes.uoc.wishlify.domain.feature.wishlists.usecase

import com.splanes.uoc.wishlify.domain.common.media.model.ImageMediaRequest
import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.WishlistItem
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.request.UpdateWishlistItemRequest

/**
 * Toggles the purchase state of a wishlist item by delegating to the generic
 * item update flow.
 */
class UpdateWishlistItemPurchaseUseCase(
  private val updateWishlistItemUseCase: UpdateWishlistItemUseCase
) : UseCase() {

  /** Toggles the purchase state of [item] inside [wishlistId]. */
  suspend operator fun invoke(
    wishlistId: String,
    item: WishlistItem
  ) = execute {
    val request = UpdateWishlistItemRequest(
      wishlist = wishlistId,
      currentItem = item,
      photo = item.photoUrl?.let { ImageMediaRequest.Url(it) },
      name = item.name,
      store = item.store,
      price = item.unitPrice,
      amount = item.amount,
      priority = item.priority,
      link = item.link,
      description = item.description,
      tags = item.tags,
      purchased = if (item.purchased != null) {
        UpdateWishlistItemRequest.Available
      } else {
        UpdateWishlistItemRequest.Purchased
      },
    )

    updateWishlistItemUseCase(request)
  }
}
