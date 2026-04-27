package com.splanes.uoc.wishlify.domain.feature.groups.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.groups.repository.GroupsRepository
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetCurrentUserIdUseCase

/**
 * Retrieves the detailed representation of a group for the current user.
 */
class FetchGroupUseCase(
  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
  private val repository: GroupsRepository
) : UseCase() {

  /**
   * Fetches the group identified by [groupId].
   */
  suspend operator fun invoke(groupId: String) = execute {
    getCurrentUserIdUseCase()
      .mapCatching { uid ->
        repository.fetchGroup(uid, groupId).getOrThrow()
      }
  }
}
