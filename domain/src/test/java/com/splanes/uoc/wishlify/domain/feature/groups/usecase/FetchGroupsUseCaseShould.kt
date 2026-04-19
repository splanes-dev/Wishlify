package com.splanes.uoc.wishlify.domain.feature.groups.usecase

import com.google.common.truth.Truth.assertThat
import com.splanes.uoc.wishlify.domain.feature.groups.model.Group
import com.splanes.uoc.wishlify.domain.feature.groups.repository.GroupsRepository
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetCurrentUserIdUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class FetchGroupsUseCaseShould {

  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase = mock()
  private val groupsRepository: GroupsRepository = mock()

  private lateinit var useCase: FetchGroupsUseCase

  @Before
  fun setup() {
    useCase = FetchGroupsUseCase(
      getCurrentUserIdUseCase = getCurrentUserIdUseCase,
      groupsRepository = groupsRepository
    )
  }

  @Test
  fun `return failure when current user id fetch fails`() = runTest {
    val error = RuntimeException()

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.failure(error))

    val result = useCase()

    assertThat(result.isFailure).isTrue()
    assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
  }

  @Test
  fun `return failure when fetch groups fails`() = runTest {
    val uid = "uid"
    val error = RuntimeException()

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.success(uid))
    whenever(groupsRepository.fetchGroups(uid)).thenReturn(Result.failure(error))

    val result = useCase()

    assertThat(result.isFailure).isTrue()
    assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
    verify(groupsRepository).fetchGroups(uid)
  }

  @Test
  fun `return groups when everything succeeds`() = runTest {
    val uid = "uid"
    val groups = listOf(mock<Group.Basic>())

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.success(uid))
    whenever(groupsRepository.fetchGroups(uid)).thenReturn(Result.success(groups))

    val result = useCase()

    assertThat(result.isSuccess).isTrue()
    assertThat(result.getOrNull()).isEqualTo(groups)
    verify(groupsRepository).fetchGroups(uid)
  }
}