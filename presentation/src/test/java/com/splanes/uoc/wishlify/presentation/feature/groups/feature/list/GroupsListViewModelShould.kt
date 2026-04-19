package com.splanes.uoc.wishlify.presentation.feature.groups.feature.list

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.splanes.uoc.wishlify.domain.feature.groups.model.Group
import com.splanes.uoc.wishlify.domain.feature.groups.usecase.FetchGroupsUseCase
import com.splanes.uoc.wishlify.domain.feature.groups.usecase.UpdateGroupUseCase
import com.splanes.uoc.wishlify.presentation.common.UnitTest
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiMapper
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class GroupsListViewModelShould : UnitTest() {

  private val fetchGroupsUseCase: FetchGroupsUseCase = mock()
  private val updateGroupUseCase: UpdateGroupUseCase = mock()
  private val errorUiMapper: ErrorUiMapper = mock()

  private lateinit var viewModel: GroupsListViewModel

  @Before
  fun setup() {
    viewModel = GroupsListViewModel(
      fetchGroupsUseCase = fetchGroupsUseCase,
      updateGroupUseCase = updateGroupUseCase,
      errorUiMapper = errorUiMapper,
    )
  }

  @Test
  fun `fetch groups when initializing and show empty when there are no groups`() = runTest {
    whenever(fetchGroupsUseCase()).thenReturn(Result.success(emptyList()))

    viewModel.uiState.test {
      val loadingState = awaitItem()
      assertThat(loadingState).isEqualTo(GroupsListUiState.Loading)

      val emptyState = awaitItem()
      assertThat(emptyState).isEqualTo(
        GroupsListUiState.Empty(
          isLoading = false,
          error = null,
        )
      )
    }
  }

  @Test
  fun `fetch groups when initializing and show groups when there are results`() = runTest {
    val groups = listOf(
      basicGroup(id = "1", name = "Actiu", isInactive = false),
      basicGroup(id = "2", name = "Inactiu", isInactive = true),
    )
    whenever(fetchGroupsUseCase()).thenReturn(Result.success(groups))

    viewModel.uiState.test {
      val loadingState = awaitItem()
      assertThat(loadingState).isEqualTo(GroupsListUiState.Loading)

      val groupsState = awaitItem()
      assertThat(groupsState).isEqualTo(
        GroupsListUiState.Groups(
          groups = listOf(
            basicGroup(id = "1", name = "Actiu", isInactive = false),
            basicGroup(id = "2", name = "Inactiu", isInactive = true),
          ),
          isLoading = false,
          error = null,
        )
      )
    }
  }

  @Test
  fun `show empty with error when fetch groups fails and there are no groups`() = runTest {
    val error = RuntimeException()
    whenever(fetchGroupsUseCase()).thenReturn(Result.failure(error))
    whenever(errorUiMapper.map(error)).thenReturn(errorUiModel())

    viewModel.uiState.test {
      val loadingState = awaitItem()
      assertThat(loadingState).isEqualTo(GroupsListUiState.Loading)

      val errorState = awaitItem()
      assertThat(errorState).isEqualTo(
        GroupsListUiState.Empty(
          isLoading = false,
          error = errorUiModel(),
        )
      )
    }
  }

  @Test
  fun `fetch groups again when create group result is success`() = runTest {
    whenever(fetchGroupsUseCase()).thenReturn(Result.success(emptyList()))

    viewModel.uiState.test {

      viewModel.onCreateGroupResult(true)

      val loadingState = awaitItem()
      assertThat(loadingState).isEqualTo(GroupsListUiState.Loading)

      val emptyState = awaitItem()
      assertThat(emptyState).isEqualTo(
        GroupsListUiState.Empty(
          isLoading = false,
          error = null,
        )
      )

      verify(fetchGroupsUseCase, times(2)).invoke()
    }
  }

  @Test
  fun `do nothing when create group result is false`() = runTest {
    whenever(fetchGroupsUseCase()).thenReturn(Result.success(emptyList()))

    viewModel.uiState.test {
      awaitItem()
      awaitItem()

      viewModel.onCreateGroupResult(false)

      expectNoEvents()
      verify(fetchGroupsUseCase, times(1)).invoke()
    }
  }

  @Test
  fun `fetch groups again when group is updated`() = runTest {
    whenever(fetchGroupsUseCase()).thenReturn(Result.success(emptyList()))

    viewModel.uiState.test {
      viewModel.onGroupUpdated()

      val loadingState = awaitItem()
      assertThat(loadingState).isEqualTo(GroupsListUiState.Loading)

      val emptyState = awaitItem()
      assertThat(emptyState).isEqualTo(
        GroupsListUiState.Empty(
          isLoading = false,
          error = null,
        )
      )

      verify(fetchGroupsUseCase, times(2)).invoke()
    }
  }

  @Test
  fun `show error when leave group fails`() = runTest {
    val currentGroups = listOf(
      basicGroup(id = "1", name = "Friends", isInactive = false),
    )
    val error = RuntimeException()
    whenever(fetchGroupsUseCase()).thenReturn(Result.success(currentGroups))
    whenever(updateGroupUseCase(any())).thenReturn(Result.failure(error))
    whenever(errorUiMapper.map(error)).thenReturn(errorUiModel())

    val group = fullGroup(
      id = "1",
      name = "Friends",
      membersUid = listOf("u1", "u2"),
      photoUrl = "photo",
    )

    viewModel.uiState.test {
      awaitItem()
      awaitItem()

      viewModel.onLeaveGroup(group)

      val loadingState = awaitItem()
      assertThat(loadingState).isEqualTo(
        GroupsListUiState.Groups(
          groups = currentGroups,
          isLoading = true,
          error = null,
        )
      )

      val errorState = awaitItem()
      assertThat(errorState).isEqualTo(
        GroupsListUiState.Groups(
          groups = currentGroups,
          isLoading = false,
          error = errorUiModel(),
        )
      )
    }
  }

  @Test
  fun `dismiss current error`() = runTest {
    val error = RuntimeException()
    whenever(fetchGroupsUseCase()).thenReturn(Result.failure(error))
    whenever(errorUiMapper.map(error)).thenReturn(errorUiModel())

    viewModel.uiState.test {
      awaitItem()
      awaitItem()

      viewModel.onDismissError()

      val uiState = awaitItem()
      assertThat(uiState).isEqualTo(
        GroupsListUiState.Empty(
          isLoading = false,
          error = null,
        )
      )
    }
  }

  private fun basicGroup(
    id: String,
    name: String,
    isInactive: Boolean,
  ) = Group.Basic(
    id = id,
    name = name,
    photoUrl = null,
    state = if (isInactive) Group.State.Inactive else Group.State.Active,
    members = emptyList()
  )

  private fun fullGroup(
    id: String,
    name: String,
    membersUid: List<String>,
    photoUrl: String?,
  ) = Group.Basic(
    id = id,
    name = name,
    photoUrl = photoUrl,
    state = Group.State.Active,
    members = membersUid
  )
}