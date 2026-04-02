package com.splanes.uoc.wishlify.domain.feature.session.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.session.model.SessionState
import com.splanes.uoc.wishlify.domain.feature.session.repository.SessionRepository
import kotlinx.coroutines.flow.Flow

class GetSessionStateFlowUseCase(
  private val repository: SessionRepository
) : UseCase() {

  operator fun invoke(): Flow<SessionState> =
    repository.observeSessionState()
}