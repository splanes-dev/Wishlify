package com.splanes.uoc.wishlify.presentation.common.deeplink

sealed interface Deeplink {

  sealed interface SharedWishlist : Deeplink
  sealed interface SecretSanta : Deeplink

  data class JoinWishlistEditor(val token: String) : Deeplink

  data class JoinSharedWishlist(val token: String) : SharedWishlist

  data class JoinSecretSanta(val token: String) : SecretSanta

  data class SharedWishlistChat(val sharedWishlistId: String) : SharedWishlist

  data class SecretSantaChat(val secretSantaId: String, val chatType: String) : SecretSanta

  sealed interface Pattern {
    val regex: Regex
  }
  data object JoinWishlistEditorPattern : Pattern {
    override val regex = Regex("^/wishlist/join/.*$")
  }
  data object JoinSharedWishlistPattern : Pattern {
    override val regex = Regex("^/shared-wishlist/join/.*$")
  }
  data object JoinSecretSantaPattern : Pattern {
    override val regex = Regex("^/secret-santa/join/.*$")
  }
  data object SharedWishlistChatPattern : Pattern {
    override val regex = Regex("^/shared-wishlist/.*/chat$")
  }

  data object SecretSantaChatPattern : Pattern {
    override val regex = Regex("^/secret-santa/.*/chat$")
  }

  companion object {
    private val Patterns = listOf(
      JoinWishlistEditorPattern,
      JoinSharedWishlistPattern,
      JoinSecretSantaPattern,
      SharedWishlistChatPattern,
      SecretSantaChatPattern
    )

    fun find(url: String): Pattern? =
      Patterns.find { pattern -> url.matches(pattern.regex) }
  }
}