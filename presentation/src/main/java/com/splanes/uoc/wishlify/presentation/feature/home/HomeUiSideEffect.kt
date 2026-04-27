package com.splanes.uoc.wishlify.presentation.feature.home

import com.splanes.uoc.wishlify.presentation.common.deeplink.Deeplink

/** One-off effects emitted by the home flow to redirect into other features. */
sealed interface HomeUiSideEffect {
  /** Requests navigation back to authentication because there is no active session. */
  data object NoSession : HomeUiSideEffect
  /** Requests navigation to create a wishlist item from a shared URL. */
  data class NavToWishlistNewItemByUrl(val url: String): HomeUiSideEffect
  /** Requests navigation to create a wishlist item from a shared image URI. */
  data class NavToWishlistNewItemByUri(val uri: String): HomeUiSideEffect
  /** Requests navigation into a wishlist-related deeplink flow. */
  data class NavToWishlist(val deeplink: Deeplink.JoinWishlistEditor): HomeUiSideEffect
  /** Requests navigation into a shared-wishlist deeplink flow. */
  data class NavToSharedWishlist(val deeplink: Deeplink.SharedWishlist): HomeUiSideEffect
  /** Requests navigation into a Secret Santa deeplink flow. */
  data class NavToSecretSanta(val deeplink: Deeplink.SecretSanta): HomeUiSideEffect
}
