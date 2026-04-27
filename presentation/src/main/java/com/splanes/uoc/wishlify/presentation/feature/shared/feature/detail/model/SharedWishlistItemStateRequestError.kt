package com.splanes.uoc.wishlify.presentation.feature.shared.feature.detail.model

/**
 * Validation errors produced by item state requests that require additional user input.
 */
sealed interface SharedWishlistItemStateRequestError {
  data class ShareRequestInvalid(val max: Int) : SharedWishlistItemStateRequestError
}
