package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.mapper

import com.splanes.uoc.wishlify.domain.common.media.model.ImageMediaRequest
import com.splanes.uoc.wishlify.domain.common.utils.newUuid
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.WishlistItem
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.request.CreateWishlistItemRequest
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.request.UpdateWishlistItemRequest
import com.splanes.uoc.wishlify.presentation.common.components.ImagePicker
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.model.WishlistItemForm

class WishlistItemFormUiMapper {

  fun creationRequestOf(wishlistId: String, form: WishlistItemForm): CreateWishlistItemRequest =
    CreateWishlistItemRequest(
      wishlist = wishlistId,
      id = newUuid(),
      name = form.name,
      store = form.store,
      price = form.unitPrice,
      amount = form.amount,
      priority = form.priority,
      link = form.link,
      description = form.description,
      tags = form.tags
        .split(",")
        .map { it.trim() },
      photo = when (val res = form.photo) {
        is ImagePicker.Device -> ImageMediaRequest.Device(uri = res.uri.toString())
        is ImagePicker.Url -> ImageMediaRequest.Url(url = res.url)
        else -> null
      }
    )

  fun wishlistFormOf(item: WishlistItem): WishlistItemForm =
    WishlistItemForm(
      photo = item.photoUrl?.let(ImagePicker::Url),
      name = item.name,
      description = item.description,
      store = item.store,
      unitPrice = item.unitPrice,
      amount = item.amount,
      priority = item.priority,
      link = item.link,
      tags = item.tags.joinToString(),
    )

  fun editionRequestOf(
    wishlistId: String,
    item: WishlistItem,
    form: WishlistItemForm
  ) =
    UpdateWishlistItemRequest(
      wishlist = wishlistId,
      currentItem = item,
      name = form.name,
      store = form.store,
      price = form.unitPrice,
      amount = form.amount,
      priority = form.priority,
      link = form.link,
      description = form.description,
      tags = form.tags
        .split(",")
        .map { it.trim() },
      photo = when (val res = form.photo) {
        is ImagePicker.Device -> ImageMediaRequest.Device(uri = res.uri.toString())
        is ImagePicker.Url -> ImageMediaRequest.Url(url = res.url)
        else -> null
      },
      purchased = null
    )
}