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

class FetchGroupUseCaseShould {

  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase = mock()
  private val repository: GroupsRepository = mock()

  private lateinit var useCase: FetchGroupUseCase

  @Before
  fun setup() {
    useCase = FetchGroupUseCase(
      getCurrentUserIdUseCase = getCurrentUserIdUseCase,
      repository = repository
    )
  }

  @Test
  fun `return failure when current user id fetch fails`() = runTest {
    val error = RuntimeException()

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.failure(error))

    val result = useCase("group-id")

    assertThat(result.isFailure).isTrue()
    assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
  }

  @Test
  fun `return failure when fetch group fails`() = runTest {
    val uid = "uid"
    val groupId = "group-id"
    val error = RuntimeException()

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.success(uid))
    whenever(repository.fetchGroup(uid, groupId)).thenReturn(Result.failure(error))

    val result = useCase(groupId)

    assertThat(result.isFailure).isTrue()
    assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
    verify(repository).fetchGroup(uid, groupId)
  }

  @Test
  fun `return group when everything succeeds`() = runTest {
    val uid = "uid"
    val groupId = "group-id"
    val group = mock<Group.Detail>()

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.success(uid))
    whenever(repository.fetchGroup(uid, groupId)).thenReturn(Result.success(group))

    val result = useCase(groupId)

    assertThat(result.isSuccess).isTrue()
    assertThat(result.getOrNull()).isEqualTo(group)
    verify(repository).fetchGroup(uid, groupId)
  }
}