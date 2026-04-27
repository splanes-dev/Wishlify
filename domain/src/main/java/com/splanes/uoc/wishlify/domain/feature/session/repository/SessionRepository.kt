package com.splanes.uoc.wishlify.domain.feature.session.repository

import com.splanes.uoc.wishlify.domain.feature.session.model.SessionState
import kotlinx.coroutines.flow.Flow

/** Repository contract for observing and querying the current session state. */
interface SessionRepository {
  /** Observes changes to the current authentication session state. */
  fun observeSessionState(): Flow<SessionState>

  /** Returns the uid of the current authenticated user. */
  fun getCurrentUid(): Result<String>
}
