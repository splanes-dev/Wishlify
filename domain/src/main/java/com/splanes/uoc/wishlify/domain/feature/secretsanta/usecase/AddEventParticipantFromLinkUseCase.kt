package com.splanes.uoc.wishlify.domain.feature.secretsanta.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.secretsanta.repository.SecretSantaRepository

class AddEventParticipantFromLinkUseCase(
  private val repository: SecretSantaRepository
) : UseCase() {

  suspend operator fun invoke(token: String) = execute {
    repository.addEventParticipantByToken(token)
  }
}