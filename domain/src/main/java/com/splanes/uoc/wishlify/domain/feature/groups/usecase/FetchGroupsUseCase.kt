package com.splanes.uoc.wishlify.domain.feature.groups.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.groups.repository.GroupsRepository
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetCurrentUserIdUseCase

class FetchGroupsUseCase(
  val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
  val groupsRepository: GroupsRepository
) : UseCase() {

  suspend operator fun invoke() = execute {
    getCurrentUserIdUseCase()
      .mapCatching { uid ->
        groupsRepository.fetchGroups(uid).getOrThrow()
      }
  }
}