package com.splanes.uoc.wishlify.domain.feature.wishlists.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.repository.WishlistsRepository

class AddWishlistEditorFromLinkUseCase(private val repository: WishlistsRepository) : UseCase() {

  suspend operator fun invoke(token: String) = execute {
    repository.addWishlistEditor(token)
  }
}