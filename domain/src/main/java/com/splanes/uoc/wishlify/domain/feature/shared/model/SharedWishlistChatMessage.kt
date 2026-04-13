package com.splanes.uoc.wishlify.domain.feature.shared.model

import com.splanes.uoc.wishlify.domain.feature.user.model.User
import java.util.Date

sealed class SharedWishlistChatMessage(
  open val id: String,
  open val text: String,
  open val createdAt: Date,
  open val isCurrentUserMessage: Boolean,
) {

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
