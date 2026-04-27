package com.splanes.uoc.wishlify.domain.feature.wishlists.model

import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.domain.common.model.InviteLink
import com.splanes.uoc.wishlify.domain.feature.user.model.User
import java.time.Instant
import java.util.Date

/**
 * Domain representation of a wishlist.
 *
 * A wishlist may belong to the current user, to a third party, or already be
 * shared through a collaborative event.
 */
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

  /** Whether this wishlist can be shared with others. */
  fun isShareable() =
    this !is Shared && numOfItems > 0 && numOfNonPurchasedItems > 0

  /** Whether the shared wishlist deadline has already passed. */
  fun isFinished() =
    this is Shared && deadline.toInstant().isBefore(Instant.now())

  /** Returns the gifting target when the wishlist is not the current user's own wishlist. */
  fun targetOrNull() = when (this) {
    is Own -> null
    is Shared -> target
    is ThirdParty -> target
  }

  /** Wishlist owned by the current user. */
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

  /** Wishlist created for another person and editable by the current user. */
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

  /** Wishlist currently linked to a collaborative sharing flow. */
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

  /** Category assignment with ownership metadata. */
  data class WishlistCategory(
    val category: Category,
    val owner: String,
    val isOwn: Boolean,
  )

  /** Metadata about the latest update performed on the wishlist. */
  data class UpdateMetadata(
    val updatedBy: User.Basic,
    val updatedAt: Date
  )

  /** Event that currently exposes this wishlist outside the private flow. */
  sealed interface ShareEvent
  /** Event metadata for a shared wishlist flow. */
  data class SharedWishlistEvent(
    val id: String,
    val inviteLink: InviteLink,
  ) : ShareEvent
  /** Event metadata for a Secret Santa flow. */
  data class SecretSantaEvent(val id: String) : ShareEvent
}
