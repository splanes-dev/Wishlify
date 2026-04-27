package com.splanes.uoc.wishlify.domain.feature.session.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.session.repository.SessionRepository

/** Retrieves the uid of the current authenticated user. */
class GetCurrentUserIdUseCase(
  private val repository: SessionRepository,
) : UseCase() {

  /** Returns the current user identifier. */
  operator fun invoke(): Result<String> =
    repository.getCurrentUid()
}
