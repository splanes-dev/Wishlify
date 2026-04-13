package com.splanes.uoc.wishlify.data.feature.session.repository

import com.splanes.uoc.wishlify.data.feature.session.datasource.SessionDataSource
import com.splanes.uoc.wishlify.domain.feature.session.model.SessionState
import com.splanes.uoc.wishlify.domain.feature.session.repository.SessionRepository
import kotlinx.coroutines.flow.Flow

class SessionRepositoryImpl(
  private val dataSource: SessionDataSource
) : SessionRepository {

  override fun observeSessionState(): Flow<SessionState> =
    dataSource.observeAuthState()

  override fun getCurrentUid(): Result<String> =
    runCatching {
      dataSource.getCurrentUserUidOrThrow()
    }
}