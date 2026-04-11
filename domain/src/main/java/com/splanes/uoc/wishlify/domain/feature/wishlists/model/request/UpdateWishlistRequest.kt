package com.splanes.uoc.wishlify.domain.feature.wishlists.model.request

import com.splanes.uoc.wishlify.domain.common.media.model.ImageMediaRequest
import com.splanes.uoc.wishlify.domain.common.model.InviteLink
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Category
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Wishlist

sealed class UpdateWishlistRequest(
  open val currentWishlist: Wishlist,
  open val title: String,
  open val description: String,
  open val media: ImageMediaRequest,
  open val category: Category?,
  open val editorInviteLink: InviteLink,
) {

  data class Own(
    override val currentWishlist: Wishlist,
    override val title: String,
    override val description: String,
    override val media: ImageMediaRequest,
    override val category: Category?,
    override val editorInviteLink: InviteLink,
  ) : UpdateWishlistRequest(
    currentWishlist = currentWishlist,
    title = title,
    description = description,
    media = media,
    category = category,
    editorInviteLink = editorInviteLink,
  )

  data class ThirdParty(
    override val currentWishlist: Wishlist,
    override val title: String,
    override val description: String,
    override val media: ImageMediaRequest,
    override val category: Category?,
    override val editorInviteLink: InviteLink,
    val target: String,
  ) : UpdateWishlistRequest(
    currentWishlist = currentWishlist,
    title = title,
    description = description,
    media = media,
    category = category,
    editorInviteLink = editorInviteLink,
  )
}