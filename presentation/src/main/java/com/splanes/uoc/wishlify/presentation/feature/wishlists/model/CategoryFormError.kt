package com.splanes.uoc.wishlify.presentation.feature.wishlists.model

/**
 * Typed validation errors for category creation and edition forms.
 */
sealed interface CategoryFormError {
  data object AlreadyExists : CategoryFormError
  data object NameLength : CategoryFormError
}
