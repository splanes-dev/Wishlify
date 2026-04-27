package com.splanes.uoc.wishlify.domain.feature.secretsanta.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.secretsanta.repository.SecretSantaRepository
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetCurrentUserIdUseCase

/** Retrieves the detailed representation of a Secret Santa event for the current user. */
class FetchSecretSantaDetailUseCase(
  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
  private val repository: SecretSantaRepository
) : UseCase() {

  /** Fetches the event identified by [eventId]. */
  suspend operator fun invoke(eventId: String) = execute {
    getCurrentUserIdUseCase()
      .mapCatching { uid ->
        repository.fetchSecretSantaEvent(uid, eventId).getOrThrow()
      }
  }
}
