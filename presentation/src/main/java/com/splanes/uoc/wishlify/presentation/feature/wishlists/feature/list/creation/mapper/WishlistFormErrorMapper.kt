package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.creation.mapper

import android.content.Context
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.creation.model.DescriptionWishlistFormError
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.creation.model.NameWishlistFormError
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.creation.model.TargetWishlistFormError
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.creation.model.WishlistFormError

class WishlistFormErrorMapper(private val context: Context) {

  fun map(error: WishlistFormError): String {
    val resources = context.resources
    return when (error) {
      DescriptionWishlistFormError.Length ->
        resources.getString(R.string.input_error_length, 3, 200)

      TargetWishlistFormError.Blank ->
        resources.getString(R.string.input_error_mandatory)

      NameWishlistFormError.Length ->
        resources.getString(R.string.input_error_length, 3, 20)
    }
  }
}