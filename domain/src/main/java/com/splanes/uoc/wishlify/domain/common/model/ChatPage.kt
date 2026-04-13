package com.splanes.uoc.wishlify.domain.common.model

data class ChatPage<T>(
  val messages: List<T>,
  val nextCursor: Long?,
  val hasMore: Boolean,
)