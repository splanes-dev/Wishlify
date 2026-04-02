package com.splanes.uoc.wishlify.presentation.feature.wishlists.model

sealed interface CategoryFormError {
  data object AlreadyExists : CategoryFormError
  data object NameLength : CategoryFormError
}