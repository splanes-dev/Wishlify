package com.splanes.uoc.wishlify.domain.feature.session.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.session.repository.SessionRepository

class GetCurrentUserIdUseCase(
  private val repository: SessionRepository,
) : UseCase() {

  operator fun invoke(): Result<String> =
    repository.getCurrentUid()
}