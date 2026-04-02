package com.splanes.uoc.wishlify.domain.feature.wishlists.model

import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.domain.common.model.InviteLink
import java.util.Date

sealed class Wishlist(
  open val id: String,
  open val title: String,
  open val description: String,
  open val photo: ImageMedia,
  open val category: Category,
  open val editorInviteLink: InviteLink,
  open val editors: List<Any>, // TODO: Basic info: uid & username?
  open val createdBy: Any, // TODO: Same than above
  open val createdAt: Date,
  open val lastUpdate: UpdateMetadata
) {

  data class Own(
    override val id: String,
    override val title: String,
    override val description: String,
    override val photo: ImageMedia,
    override val category: Category,
    override val editorInviteLink: InviteLink,
    override val editors: List<Any>,
    override val createdBy: Any,
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
    override val category: Category,
    override val editorInviteLink: InviteLink,
    override val editors: List<Any>,
    override val createdBy: Any,
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

  data class UpdateMetadata(
    val updatedBy: Any, // TODO: Same
    val updatedAt: Date
  )
}
