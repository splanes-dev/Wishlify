package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.share.mapper

import android.content.Context
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.share.model.DateWishlistShareFormError
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.share.model.WishlistShareFormError

class WishlistShareFormUiMapper(private val context: Context) {

  fun map(error: WishlistShareFormError): String {
    val resources = context.resources
    return when (error) {
      DateWishlistShareFormError.Blank ->
        resources.getString(R.string.input_error_mandatory)

      DateWishlistShareFormError.Invalid ->
        resources.getString(R.string.wishlists_share_date_limit_invalid_error)
    }
  }
}