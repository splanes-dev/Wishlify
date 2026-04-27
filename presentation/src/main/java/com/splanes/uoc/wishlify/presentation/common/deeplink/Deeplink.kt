package com.splanes.uoc.wishlify.presentation.common.deeplink

/**
 * Presentation-level model of the deeplinks supported by the app.
 *
 * It groups both join flows and direct navigation targets for shared wishlists
 * and Secret Santa features.
 */
sealed interface Deeplink {

  /** Marker for deeplinks that belong to the shared-wishlist flow. */
  sealed interface SharedWishlist : Deeplink
  /** Marker for deeplinks that belong to the Secret Santa flow. */
  sealed interface SecretSanta : Deeplink

  /** Deeplink that joins the current user as editor of a wishlist. */
  data class JoinWishlistEditor(val token: String) : Deeplink

  /** Deeplink that joins the current user as participant of a shared wishlist. */
  data class JoinSharedWishlist(val token: String) : SharedWishlist

  /** Deeplink that joins the current user as participant of a Secret Santa event. */
  data class JoinSecretSanta(val token: String) : SecretSanta

  /** Deeplink that opens the detail screen of a shared wishlist. */
  data class SharedWishlistDetail(val sharedWishlistId: String) : SharedWishlist

  /** Deeplink that opens the chat screen of a shared wishlist. */
  data class SharedWishlistChat(val sharedWishlistId: String) : SharedWishlist

  /** Deeplink that opens the detail screen of a Secret Santa event. */
  data class SecretSantaDetail(val secretSantaId: String) : SecretSanta

  /** Deeplink that opens a Secret Santa chat with the requested perspective. */
  data class SecretSantaChat(val secretSantaId: String, val chatType: String) : SecretSanta

  /** Contract implemented by the supported deeplink URL patterns. */
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

    /** Finds the first supported deeplink pattern that matches the provided path. */
    fun find(url: String): Pattern? =
      Patterns.find { pattern -> url.matches(pattern.regex) }
  }
}
