package com.splanes.uoc.wishlify.domain.feature.wishlists.model

import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.domain.common.model.InviteLink
import com.splanes.uoc.wishlify.domain.feature.user.model.User
import java.util.Date

sealed class Wishlist(
  open val id: String,
  open val title: String,
  open val description: String,
  open val photo: ImageMedia,
  open val category: WishlistCategory?,
  open val editorInviteLink: InviteLink,
  open val editors: List<User.Basic>,
  open val createdBy: User.Basic,
  open val createdAt: Date,
  open val lastUpdate: UpdateMetadata
) {

  data class Own(
    override val id: String,
    override val title: String,
    override val description: String,
    override val photo: ImageMedia,
    override val category: WishlistCategory?,
    override val editorInviteLink: InviteLink,
    override val editors: List<User.Basic>,
    override val createdBy: User.Basic,
    override val createdAt: Date,
    override val lastUpdate: UpdateMetadata,
  ) : Wishlist(
    id = id,
    title = title,
    description = description,
    photo = photo,
    category = category,
    editorInviteLink = editorInviteLink,
    editors = editors,
    createdBy = createdBy,
    createdAt = createdAt,
    lastUpdate = lastUpdate,
  )

  data class ThirdParty(
    override val id: String,
    override val title: String,
    override val description: String,
    override val photo: ImageMedia,
    override val category: WishlistCategory?,
    override val editorInviteLink: InviteLink,
    override val editors: List<User.Basic>,
    override val createdBy: User.Basic,
    override val createdAt: Date,
    override val lastUpdate: UpdateMetadata,
    val target: String,
  ) : Wishlist(
    id = id,
    title = title,
    description = description,
    photo = photo,
    category = category,
    editorInviteLink = editorInviteLink,
    editors = editors,
    createdBy = createdBy,
    createdAt = createdAt,
    lastUpdate = lastUpdate,
  )

  data class WishlistCategory(
    val category: Category,
    val owner: String,
    val isOwn: Boolean,
  )

  data class UpdateMetadata(
    val updatedBy: User.Basic,
    val updatedAt: Date
  )
}
