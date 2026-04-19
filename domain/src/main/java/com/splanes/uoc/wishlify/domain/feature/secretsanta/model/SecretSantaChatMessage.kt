package com.splanes.uoc.wishlify.domain.feature.secretsanta.model

import com.splanes.uoc.wishlify.domain.feature.user.model.User
import java.util.Date

data class SecretSantaChatMessage(
  val chatId: String,
  val messageId: String,
  val sender: User.Basic,
  val text: String,
  val sentAt: Date,
  val isCurrentUserMessage: Boolean,
)
