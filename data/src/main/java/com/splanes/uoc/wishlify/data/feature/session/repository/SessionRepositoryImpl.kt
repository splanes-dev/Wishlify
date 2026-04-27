package com.splanes.uoc.wishlify.data.feature.session.repository

import com.splanes.uoc.wishlify.data.feature.session.datasource.SessionDataSource
import com.splanes.uoc.wishlify.domain.feature.session.model.SessionState
import com.splanes.uoc.wishlify.domain.feature.session.repository.SessionRepository
import kotlinx.coroutines.flow.Flow

/** Data-layer implementation of [SessionRepository] backed by Firebase Auth. */
class SessionRepositoryImpl(
  private val dataSource: SessionDataSource
) : SessionRepository {

  /** Delegates session observation to the Firebase-backed data source. */
  override fun observeSessionState(): Flow<SessionState> =
    dataSource.observeAuthState()

  /** Resolves the current authenticated user id when a session is available. */
  override fun getCurrentUid(): Result<String> =
    runCatching {
      dataSource.getCurrentUserUidOrThrow()
    }
}
