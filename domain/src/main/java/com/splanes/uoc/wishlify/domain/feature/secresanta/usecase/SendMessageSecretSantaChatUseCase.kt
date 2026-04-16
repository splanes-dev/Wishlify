package com.splanes.uoc.wishlify.domain.feature.secresanta.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.secresanta.helper.SecretSantaChatIdBuilder
import com.splanes.uoc.wishlify.domain.feature.secresanta.model.SecretSantaSendMessageRequest
import com.splanes.uoc.wishlify.domain.feature.secresanta.repository.SecretSantaRepository
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetCurrentUserIdUseCase

class SendMessageSecretSantaChatUseCase(
  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
  private val chatIdBuilder: SecretSantaChatIdBuilder,
  private val repository: SecretSantaRepository,
) : UseCase() {

  suspend operator fun invoke(request: SecretSantaSendMessageRequest) = execute {
    getCurrentUserIdUseCase().mapCatching { uid ->

      val chatId = when (request) {
        is SecretSantaSendMessageRequest.AsGiver ->
          uid to request.otherUid

        is SecretSantaSendMessageRequest.AsReceiver ->
          request.otherUid to uid
      }.let { (giver, receiver) -> chatIdBuilder.build(giver, receiver) }

      repository.sendMessageToChat(
        uid = uid,
        eventId = request.eventId,
        chatId = chatId,
        text = request.text,
      ).getOrThrow()
    }
  }
}