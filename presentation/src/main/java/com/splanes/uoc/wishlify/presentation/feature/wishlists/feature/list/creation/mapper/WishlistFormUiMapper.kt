package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.creation.mapper

import com.splanes.uoc.wishlify.domain.common.media.model.ImageMediaRequest
import com.splanes.uoc.wishlify.domain.common.model.InviteLink
import com.splanes.uoc.wishlify.domain.common.utils.newUuid
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Category
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Wishlist
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.request.CreateWishlistRequest
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.request.UpdateWishlistRequest
import com.splanes.uoc.wishlify.presentation.common.components.ImagePicker
import com.splanes.uoc.wishlify.presentation.common.components.image.ImagePreset
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.creation.model.WishlistsNewListForm

class WishlistFormUiMapper {

  fun createWishlistRequestOf(
    isOwnWishlist: Boolean,
    categories: List<Category>,
    editorLink: InviteLink,
    form: WishlistsNewListForm
  ): CreateWishlistRequest =
    if (isOwnWishlist) {
      CreateWishlistRequest.Own(
        id = newUuid(),
        title = form.name,
        description = form.description.orEmpty(),
        media = form.image?.let { resource ->
          when (resource) {
            is ImagePicker.Device -> ImageMediaRequest.Device(uri = resource.uri.toString())
            is ImagePicker.Preset -> ImageMediaRequest.Preset(id = resource.id.toString())
            is ImagePicker.Url -> ImageMediaRequest.Url(url = resource.url)
          }
        } ?: ImageMediaRequest.Preset(id = ImagePreset.Gift.id.toString()),
        category = form.categoryIndex?.let { index -> categories.getOrNull(index) },
        editorInviteLink = editorLink
      )
    } else {
      CreateWishlistRequest.ThirdParty(
        id = newUuid(),
        title = form.name,
        description = form.description.orEmpty(),
        media = form.image?.let { resource ->
          when (resource) {
            is ImagePicker.Device -> ImageMediaRequest.Device(uri = resource.uri.toString())
            is ImagePicker.Preset -> ImageMediaRequest.Preset(id = resource.id.toString())
            is ImagePicker.Url -> ImageMediaRequest.Url(url = resource.url)
          }
        } ?: ImageMediaRequest.Preset(id = ImagePreset.Gift.id.toString()),
        category = form.categoryIndex?.let { index -> categories.getOrNull(index) },
        editorInviteLink = editorLink,
        target = form.target.orEmpty()
      )
    }

  fun updateWishlistRequestOf(
    currentWishlist: Wishlist,
    isOwnWishlist: Boolean,
    categories: List<Category>,
    editorLink: InviteLink,
    form: WishlistsNewListForm
  ): UpdateWishlistRequest =
    if (isOwnWishlist) {
      UpdateWishlistRequest.Own(
        currentWishlist = currentWishlist,
        title = form.name,
        description = form.description.orEmpty(),
        media = form.image?.let { resource ->
          when (resource) {
            is ImagePicker.Device -> ImageMediaRequest.Device(uri = resource.uri.toString())
            is ImagePicker.Preset -> ImageMediaRequest.Preset(id = resource.id.toString())
            is ImagePicker.Url -> ImageMediaRequest.Url(url = resource.url)
          }
        } ?: ImageMediaRequest.Preset(id = ImagePreset.Gift.id.toString()),
        category = form.categoryIndex?.let { index -> categories.getOrNull(index) },
        editorInviteLink = editorLink
      )
    } else {
      UpdateWishlistRequest.ThirdParty(
        currentWishlist = currentWishlist,
        title = form.name,
        description = form.description.orEmpty(),
        media = form.image?.let { resource ->
          when (resource) {
            is ImagePicker.Device -> ImageMediaRequest.Device(uri = resource.uri.toString())
            is ImagePicker.Preset -> ImageMediaRequest.Preset(id = resource.id.toString())
            is ImagePicker.Url -> ImageMediaRequest.Url(url = resource.url)
          }
        } ?: ImageMediaRequest.Preset(id = ImagePreset.Gift.id.toString()),
        category = form.categoryIndex?.let { index -> categories.getOrNull(index) },
        editorInviteLink = editorLink,
        target = form.target.orEmpty()
      )
    }
}