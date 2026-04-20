package com.splanes.uoc.wishlify.presentation.feature.shared.feature.detail.model

sealed interface SharedWishlistItemStateRequestError {
  data class ShareRequestInvalid(val max: Int) : SharedWishlistItemStateRequestError
}

