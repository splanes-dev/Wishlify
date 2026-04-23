package com.splanes.uoc.wishlify.presentation.feature.home

import com.splanes.uoc.wishlify.presentation.common.deeplink.Deeplink

sealed interface HomeUiSideEffect {
  data object NoSession : HomeUiSideEffect
  data class NavToWishlistNewItemByUrl(val url: String): HomeUiSideEffect
  data class NavToWishlistNewItemByUri(val uri: String): HomeUiSideEffect
  data class NavToWishlist(val deeplink: Deeplink.JoinWishlistEditor): HomeUiSideEffect
  data class NavToSharedWishlist(val deeplink: Deeplink.SharedWishlist): HomeUiSideEffect
  data class NavToSecretSanta(val deeplink: Deeplink.SecretSanta): HomeUiSideEffect
}