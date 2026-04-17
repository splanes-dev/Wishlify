package com.splanes.uoc.wishlify.domain.feature.groups.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.groups.repository.GroupsRepository
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetCurrentUserIdUseCase

class FetchGroupUseCase(
  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
  private val repository: GroupsRepository
) : UseCase() {

  suspend operator fun invoke(groupId: String) = execute {
    getCurrentUserIdUseCase()
      .mapCatching { uid ->
        repository.fetchGroup(uid, groupId).getOrThrow()
      }
  }
}