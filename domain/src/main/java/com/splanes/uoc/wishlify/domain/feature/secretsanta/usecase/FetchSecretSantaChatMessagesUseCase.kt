package com.splanes.uoc.wishlify.domain.feature.secretsanta.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.secretsanta.helper.SecretSantaChatIdBuilder
import com.splanes.uoc.wishlify.domain.feature.secretsanta.model.GetSecretSantaChatRequest
import com.splanes.uoc.wishlify.domain.feature.secretsanta.repository.SecretSantaRepository
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetCurrentUserIdUseCase

/**
 * Fetches a paginated batch of messages from a Secret Santa chat.
 *
 * The final chat id depends on whether the current user is acting as giver or receiver.
 */
class FetchSecretSantaChatMessagesUseCase(
  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
  private val chatIdBuilder: SecretSantaChatIdBuilder,
  private val repository: SecretSantaRepository,
) : UseCase() {

  /** Fetches chat messages using the role-aware [request] and pagination cursor. */
  suspend operator fun invoke(request: GetSecretSantaChatRequest, cursor: Long, limit: Int = 30) = execute {
    getCurrentUserIdUseCase().mapCatching { uid ->

      val chatId = when (request) {
        is GetSecretSantaChatRequest.AsGiver ->
          uid to request.otherUid

        is GetSecretSantaChatRequest.AsReceiver ->
          request.otherUid to uid
      }.let { (giver, receiver) -> chatIdBuilder.build(giver, receiver) }

      repository.fetchSecretSantaChatMessages(
        uid = uid,
        eventId = request.eventId,
        chatId = chatId,
        cursor = cursor,
        limit = limit
      ).getOrThrow()
    }
  }
}
