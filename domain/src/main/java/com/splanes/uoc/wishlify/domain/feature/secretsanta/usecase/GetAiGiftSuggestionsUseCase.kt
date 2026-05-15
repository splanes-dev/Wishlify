package com.splanes.uoc.wishlify.domain.feature.secretsanta.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.ia.repository.AiRepository
import com.splanes.uoc.wishlify.domain.feature.secretsanta.repository.SecretSantaRepository
import timber.log.Timber

class GetAiGiftSuggestionsUseCase(
  private val repository: SecretSantaRepository,
  private val aiRepository: AiRepository
) : UseCase() {

  suspend operator fun invoke(
    target: String,
    eventId: String
  ) = execute {
    repository.getGiftSuggestionsAiContext(target, eventId).mapCatching { context ->
      aiRepository.getGiftSuggestions(context.aiContext).also {
        Timber.d("AiContext: ${context.aiContext}")
        Timber.d("Result: ${it.map { r -> r.toString() }}")
      }
    }
  }
}
