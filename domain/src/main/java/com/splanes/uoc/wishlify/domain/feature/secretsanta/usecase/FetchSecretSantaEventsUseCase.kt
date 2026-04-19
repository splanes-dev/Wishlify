package com.splanes.uoc.wishlify.domain.feature.secretsanta.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.secretsanta.repository.SecretSantaRepository
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetCurrentUserIdUseCase

class FetchSecretSantaEventsUseCase(
  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
  private val repository: SecretSantaRepository,
) : UseCase() {

  suspend operator fun invoke() = execute {
    getCurrentUserIdUseCase()
      .mapCatching { uid ->
        repository.fetchSecretSantaEvents(uid).getOrThrow()
      }
  }
}