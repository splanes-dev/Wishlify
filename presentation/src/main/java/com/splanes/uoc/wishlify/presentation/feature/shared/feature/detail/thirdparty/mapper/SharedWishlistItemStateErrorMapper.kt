package com.splanes.uoc.wishlify.presentation.feature.shared.feature.detail.thirdparty.mapper

import android.content.Context
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.feature.shared.feature.detail.thirdparty.model.SharedWishlistItemStateRequestError

class SharedWishlistItemStateErrorMapper(private val context: Context) {

  fun map(error: SharedWishlistItemStateRequestError): String {
    val resources = context.resources
    return when (error) {
      is SharedWishlistItemStateRequestError.ShareRequestInvalid ->
        resources.getString(
          R.string.shared_wishlists_item_state_request_share_num_of_participants_error,
          error.max
        )
    }
  }
}