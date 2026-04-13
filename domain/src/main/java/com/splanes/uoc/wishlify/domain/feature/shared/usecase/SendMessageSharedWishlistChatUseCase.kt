package com.splanes.uoc.wishlify.domain.feature.shared.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetCurrentUserIdUseCase
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlistSendMessageRequest
import com.splanes.uoc.wishlify.domain.feature.shared.repository.SharedWishlistsRepository

class SendMessageSharedWishlistChatUseCase(
  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
  private val repository: SharedWishlistsRepository,
) : UseCase() {

  suspend operator fun invoke(request: SharedWishlistSendMessageRequest) = execute {
    getCurrentUserIdUseCase()
      .mapCatching { uid -> repository.sendMessageToChat(uid, request).getOrThrow() }
  }
}