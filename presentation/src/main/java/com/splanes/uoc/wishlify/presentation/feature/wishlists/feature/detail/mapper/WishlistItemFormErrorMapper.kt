package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.mapper

import android.content.Context
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.model.AmountWishlistItemFormError
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.model.DescriptionWishlistItemFormError
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.model.LinkWishlistItemFormError
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.model.NameWishlistItemFormError
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.model.PriceWishlistItemFormError
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.model.StoreWishlistItemFormError
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.model.TagsWishlistItemFormError
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.model.WishlistItemFormError
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.model.WishlistItemFormErrors
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.model.WishlistItemUiFormErrors

class WishlistItemFormErrorMapper(private val context: Context) {

  fun map(errors: WishlistItemFormErrors): WishlistItemUiFormErrors =
    WishlistItemUiFormErrors(
      name = errors.name?.let(::map),
      description = errors.description?.let(::map),
      store = errors.store?.let(::map),
      unitPrice = errors.unitPrice?.let(::map),
      amount = errors.amount?.let(::map),
      link = errors.link?.let(::map),
      tags = errors.tags?.let(::map),
    )

  fun map(error: WishlistItemFormError): String {
    val resources = context.resources
    return when (error) {
      AmountWishlistItemFormError.Invalid ->
        resources.getString(R.string.input_error_invalid_format)

      DescriptionWishlistItemFormError.Length ->
        resources.getString(R.string.input_error_length, 3, 200)

      LinkWishlistItemFormError.Invalid ->
        resources.getString(R.string.input_error_invalid_format)

      NameWishlistItemFormError.Length ->
        resources.getString(R.string.input_error_length, 3, 50)

      PriceWishlistItemFormError.Blank -> {
        resources.getString(R.string.input_error_mandatory)
      }

      PriceWishlistItemFormError.Invalid ->
        resources.getString(R.string.input_error_invalid_format)

      StoreWishlistItemFormError.Length ->
        resources.getString(R.string.input_error_length, 3, 30)

      TagsWishlistItemFormError.Count ->
        resources.getString(R.string.wishlists_new_item_tags_input_error)
    }
  }
}