package com.splanes.uoc.wishlify.domain.feature.wishlists.model.request

import com.splanes.uoc.wishlify.domain.common.media.model.ImageMediaRequest
import com.splanes.uoc.wishlify.domain.common.model.InviteLink
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Category

/** Input required to create a wishlist. */
sealed class CreateWishlistRequest(
  open val id: String,
  open val title: String,
  open val description: String,
  open val media: ImageMediaRequest,
  open val category: Category?,
  open val editorInviteLink: InviteLink,
) {

  /** Request to create a wishlist owned by the current user. */
  data class Own(
    override val id: String,
    override val title: String,
    override val description: String,
    override val media: ImageMediaRequest,
    override val category: Category?,
    override val editorInviteLink: InviteLink,
  ) : CreateWishlistRequest(
    id = id,
    title = title,
    description = description,
    media = media,
    category = category,
    editorInviteLink = editorInviteLink,
  )

  /** Request to create a wishlist for a third party. */
  data class ThirdParty(
    override val id: String,
    override val title: String,
    override val description: String,
    override val media: ImageMediaRequest,
    override val category: Category?,
    override val editorInviteLink: InviteLink,
    val target: String,
  ) : CreateWishlistRequest(
    id = id,
    title = title,
    description = description,
    media = media,
    category = category,
    editorInviteLink = editorInviteLink,
  )
}
