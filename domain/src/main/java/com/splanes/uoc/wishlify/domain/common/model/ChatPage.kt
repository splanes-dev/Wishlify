package com.splanes.uoc.wishlify.domain.common.model

/**
 * Cursor-based page of chat messages.
 *
 * @param messages Messages returned for the current page.
 * @param nextCursor Cursor to request the next page, or `null` when pagination cannot continue.
 * @param hasMore Whether more messages are available after this page.
 */
data class ChatPage<T>(
  val messages: List<T>,
  val nextCursor: Long?,
  val hasMore: Boolean,
)
