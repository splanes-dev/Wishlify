package com.splanes.uoc.wishlify.domain.feature.groups.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.groups.repository.GroupsRepository
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetCurrentUserIdUseCase

/**
 * Retrieves the groups available to the current user.
 */
class FetchGroupsUseCase(
  val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
  val groupsRepository: GroupsRepository
) : UseCase() {

  /**
   * Fetches the current user's groups.
   */
  suspend operator fun invoke() = execute {
    getCurrentUserIdUseCase()
      .mapCatching { uid ->
        groupsRepository.fetchGroups(uid).getOrThrow()
      }
  }
}
