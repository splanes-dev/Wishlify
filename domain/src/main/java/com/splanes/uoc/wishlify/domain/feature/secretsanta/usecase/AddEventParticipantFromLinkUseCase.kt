package com.splanes.uoc.wishlify.domain.feature.secretsanta.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.secretsanta.repository.SecretSantaRepository

/** Joins a Secret Santa event through an invitation token. */
class AddEventParticipantFromLinkUseCase(
  private val repository: SecretSantaRepository
) : UseCase() {

  /** Adds the current user to the event referenced by [token]. */
  suspend operator fun invoke(token: String) = execute {
    repository.addEventParticipantByToken(token)
  }
}
