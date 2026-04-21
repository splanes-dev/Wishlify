package com.splanes.uoc.wishlify.presentation.infrastructure.navigation.actions

import android.net.Uri

sealed interface Action
sealed interface CreateNewWishlistItem : Action
data class NewWishlistItemByUrl(val url: String): CreateNewWishlistItem
data class NewWishlistItemByImage(val uri: Uri): CreateNewWishlistItem
data class OpenDeeplink(val deeplink: Uri) : Action