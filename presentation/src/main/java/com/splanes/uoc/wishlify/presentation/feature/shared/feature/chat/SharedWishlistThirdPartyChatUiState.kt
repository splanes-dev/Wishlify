package com.splanes.uoc.wishlify.presentation.feature.shared.feature.chat

import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlistChatMessage

/**
 * UI state for the third-party shared wishlist chat flow.
 */
sealed interface SharedWishlistThirdPartyChatUiState {

  data class Loading(
    val wishlistName: String,
    val target: String,
  ) : SharedWishlistThirdPartyChatUiState

  data class Error(
    val wishlistName: String,
    val target: String,
  ) : SharedWishlistThirdPartyChatUiState

  data class Empty(
    val wishlistName: String,
    val target: String,
  ) : SharedWishlistThirdPartyChatUiState

  data class Chat(
    val wishlistName: String,
    val target: String,
    val messages: List<SharedWishlistChatMessage>,
    val isLoading: Boolean,
    val canLoadOlderMessages: Boolean,
  ) : SharedWishlistThirdPartyChatUiState
}
