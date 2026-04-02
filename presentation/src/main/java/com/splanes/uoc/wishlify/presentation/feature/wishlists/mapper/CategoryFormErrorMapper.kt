package com.splanes.uoc.wishlify.presentation.feature.wishlists.mapper

import android.content.Context
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.feature.wishlists.model.CategoryFormError

class CategoryFormErrorMapper(private val context: Context) {

  fun map(error: CategoryFormError): String {
    val resources = context.resources
    return when (error) {
      CategoryFormError.AlreadyExists ->
        resources.getString(R.string.wishlists_create_category_already_exists_error)
      CategoryFormError.NameLength ->
        resources.getString(R.string.input_error_length, 3, 20)
    }
  }
}