package com.splanes.uoc.wishlify.domain.feature.wishlists.model

import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.domain.common.model.InviteLink
import com.splanes.uoc.wishlify.domain.feature.user.model.User
import java.time.Instant
import java.util.Date

sealed class Wishlist(
  open val id: String,
  open val title: String,
  open val description: String,
  open val photo: ImageMedia,
  open val category: WishlistCategory?,
  open val editorInviteLink: InviteLink,
  open val editors: List<User.Basic>,
  open val numOfNonPurchasedItems: Int,
  open val numOfItems: Int,
  open val createdBy: User.Basic,
  open val createdAt: Date,
  open val lastUpdate: UpdateMetadata
) {

  fun isShareable() =
    this !is Shared && numOfItems > 0 && numOfNonPurchasedItems > 0

  fun isFinished() =
    this is Shared && deadline.toInstant().isBefore(Instant.now())

  fun targetOrNull() = when (this) {
    is Own -> null
    is Shared -> target
    is ThirdParty -> target
  }

  data class Own(
    override val id: String,
    override val title: String,
    override val description: String,
    override val photo: ImageMedia,
    override val category: WishlistCategory?,
    override val editorInviteLink: InviteLink,
    override val editors: List<User.Basic>,
    override val numOfNonPurchasedItems: Int,
    override val numOfItems: Int,
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
    numOfNonPurchasedItems = numOfNonPurchasedItems,
    numOfItems = numOfItems,
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
    override val numOfNonPurchasedItems: Int,
    override val numOfItems: Int,
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
    numOfItems = numOfItems,
    numOfNonPurchasedItems = numOfNonPurchasedItems,
    createdBy = createdBy,
    createdAt = createdAt,
    lastUpdate = lastUpdate,
  )

  data class Shared(
    override val id: String,
    override val title: String,
    override val description: String,
    override val photo: ImageMedia,
    override val category: WishlistCategory?,
    override val editorInviteLink: InviteLink,
    override val editors: List<User.Basic>,
    override val numOfNonPurchasedItems: Int,
    override val numOfItems: Int,
    override val createdBy: User.Basic,
    override val createdAt: Date,
    override val lastUpdate: UpdateMetadata,
    val target: String?,
    val deadline: Date,
    val event: ShareEvent,
  ) : Wishlist(
    id = id,
    title = title,
    description = description,
    photo = photo,
    category = category,
    editorInviteLink = editorInviteLink,
    editors = editors,
    numOfItems = numOfItems,
    numOfNonPurchasedItems = numOfNonPurchasedItems,
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

  sealed interface ShareEvent
  data class SharedWishlistEvent(val id: String) : ShareEvent
  data class SecretSantaEvent(val id: String) : ShareEvent
}
