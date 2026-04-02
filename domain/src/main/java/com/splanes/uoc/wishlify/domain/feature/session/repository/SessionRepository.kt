package com.splanes.uoc.wishlify.domain.feature.session.repository

import com.splanes.uoc.wishlify.domain.feature.session.model.SessionState
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
  fun observeSessionState(): Flow<SessionState>
  suspend fun getCurrentUid(): Result<String>
}