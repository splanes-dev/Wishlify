package com.splanes.uoc.wishlify.domain.feature.shared.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.shared.repository.SharedWishlistsRepository

/** Joins a shared wishlist through an invitation token. */
class AddSharedWishlistParticipantByTokenUseCase(
  private val repository: SharedWishlistsRepository
) : UseCase() {

  /** Adds the current user to the shared wishlist referenced by [token]. */
  suspend operator fun invoke(token: String) = execute {
    repository.addParticipantByToken(token)
  }
}
