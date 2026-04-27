package com.splanes.uoc.wishlify.domain.feature.wishlists.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.repository.WishlistsRepository

/** Joins a wishlist as editor through an invitation token. */
class AddWishlistEditorFromLinkUseCase(private val repository: WishlistsRepository) : UseCase() {

  /** Adds the current user as editor of the wishlist referenced by [token]. */
  suspend operator fun invoke(token: String) = execute {
    repository.addWishlistEditor(token)
  }
}
