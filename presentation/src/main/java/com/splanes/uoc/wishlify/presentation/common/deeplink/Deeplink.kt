package com.splanes.uoc.wishlify.presentation.common.deeplink

sealed interface Deeplink {

  sealed interface SharedWishlist : Deeplink
  sealed interface SecretSanta : Deeplink

  data class JoinWishlistEditor(val token: String) : Deeplink

  data class JoinSharedWishlist(val token: String) : SharedWishlist

  data class JoinSecretSanta(val token: String) : SecretSanta

  data class SharedWishlistDetail(val sharedWishlistId: String) : SharedWishlist

  data class SharedWishlistChat(val sharedWishlistId: String) : SharedWishlist

  data class SecretSantaDetail(val secretSantaId: String) : SecretSanta

  data class SecretSantaChat(val secretSantaId: String, val chatType: String) : SecretSanta

  sealed interface Pattern {
    val regex: Regex
  }
  data object JoinWishlistEditorPattern : Pattern {
    override val regex = Regex("^/wishlist/join/[a-zA-Z0-9]{32}$")
  }
  data object JoinSharedWishlistPattern : Pattern {
    override val regex = Regex("^/shared-wishlist/join/[a-zA-Z0-9]{32}$")
  }
  data object JoinSecretSantaPattern : Pattern {
    override val regex = Regex("^/secret-santa/join/[a-zA-Z0-9]{32}$")
  }
  data object SharedWishlistDetailPattern : Pattern {
    override val regex = Regex("^/shared-wishlist/[a-zA-Z0-9]{32}$")
  }
  data object SharedWishlistChatPattern : Pattern {
    override val regex = Regex("^/shared-wishlist/[a-zA-Z0-9]{32}/chat$")
  }

  data object SecretSantaDetailPattern : Pattern {
    override val regex = Regex("^/secret-santa/[a-zA-Z0-9]{32}$")
  }

  data object SecretSantaChatPattern : Pattern {
    override val regex = Regex("^/secret-santa/[a-zA-Z0-9]{32}/chat$")
  }

  companion object {
    private val Patterns = listOf(
      JoinWishlistEditorPattern,
      JoinSharedWishlistPattern,
      JoinSecretSantaPattern,
      SharedWishlistDetailPattern,
      SharedWishlistChatPattern,
      SecretSantaDetailPattern,
      SecretSantaChatPattern
    )

    fun find(url: String): Pattern? =
      Patterns.find { pattern -> url.matches(pattern.regex) }
  }
}