package com.splanes.uoc.wishlify.presentation.feature.shared.feature.detail.thirdparty.model

sealed interface SharedWishlistItemStateRequestError {
  data class ShareRequestInvalid(val max: Int) : SharedWishlistItemStateRequestError
}

