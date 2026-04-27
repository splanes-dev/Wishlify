package com.splanes.uoc.wishlify.domain.feature.shared.model

import com.splanes.uoc.wishlify.domain.feature.user.model.User
import java.util.Date

/** Message exchanged in the chat associated with a shared wishlist. */
sealed class SharedWishlistChatMessage(
  open val id: String,
  open val text: String,
  open val createdAt: Date,
  open val isCurrentUserMessage: Boolean,
) {

  /** User-authored chat message. */
  data class User(
    override val id: String,
    override val text: String,
    override val createdAt: Date,
    override val isCurrentUserMessage: Boolean,
    val createdBy: User.Basic,
  ) : SharedWishlistChatMessage(
    id = id,
    text = text,
    createdAt = createdAt,
    isCurrentUserMessage = isCurrentUserMessage,
  )

  /** System-generated chat message. */
  data class System(
    override val id: String,
    override val text: String,
    override val createdAt: Date,
  ) : SharedWishlistChatMessage(
    id = id,
    text = text,
    createdAt = createdAt,
    isCurrentUserMessage = false,
  )
}
