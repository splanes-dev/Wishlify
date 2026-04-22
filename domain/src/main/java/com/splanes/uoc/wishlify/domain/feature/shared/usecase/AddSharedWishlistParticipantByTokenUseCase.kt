package com.splanes.uoc.wishlify.domain.feature.shared.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.shared.repository.SharedWishlistsRepository

class AddSharedWishlistParticipantByTokenUseCase(
  private val repository: SharedWishlistsRepository
) : UseCase() {

  suspend operator fun invoke(token: String) = execute {
    repository.addParticipantByToken(token)
  }
}