package com.splanes.uoc.wishlify.presentation.feature.groups.feature.detail

import app.cash.turbine.test
import app.cash.turbine.turbineScope
import com.google.common.truth.Truth.assertThat
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMediaRequest
import com.splanes.uoc.wishlify.domain.feature.groups.model.Group
import com.splanes.uoc.wishlify.domain.feature.groups.model.UpdateGroupRequest
import com.splanes.uoc.wishlify.domain.feature.groups.usecase.FetchGroupUseCase
import com.splanes.uoc.wishlify.domain.feature.groups.usecase.UpdateGroupUseCase
import com.splanes.uoc.wishlify.domain.feature.secretsanta.usecase.FetchSecretSantaEventsUseCase
import com.splanes.uoc.wishlify.domain.feature.shared.usecase.FetchSharedWishlistsUseCase
import com.splanes.uoc.wishlify.domain.feature.user.model.User
import com.splanes.uoc.wishlify.presentation.common.UnitTest
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiMapper
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.internal.verification.AtMost
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class GroupDetailViewModelShould : UnitTest() {

  private val groupId = "group-id"
  private val groupName = "Friends"

  private val fetchGroupUseCase: FetchGroupUseCase = mock()
  private val updateGroupUseCase: UpdateGroupUseCase = mock()
  private val fetchSharedWishlistsUseCase: FetchSharedWishlistsUseCase = mock()
  private val fetchSecretSantaEventsUseCase: FetchSecretSantaEventsUseCase = mock()
  private val errorUiMapper: ErrorUiMapper = mock()

  private lateinit var viewModel: GroupDetailViewModel

  @Before
  fun setup() {
    viewModel = GroupDetailViewModel(
      groupId = groupId,
      groupName = groupName,
      fetchGroupUseCase = fetchGroupUseCase,
      updateGroupUseCase = updateGroupUseCase,
      fetchSharedWishlistsUseCase = fetchSharedWishlistsUseCase,
      fetchSecretSantaEventsUseCase = fetchSecretSantaEventsUseCase,
      errorUiMapper = errorUiMapper,
    )
  }

  @Test
  fun `fetch group when initializing and show detail when success`() = runTest {
    val group = detailGroup(id = groupId, name = groupName)
    whenever(fetchGroupUseCase(groupId)).thenReturn(Result.success(group))

    viewModel.uiState.test {
      val loadingState = awaitItem()
      assertThat(loadingState).isEqualTo(
        GroupDetailUiState.Loading(groupName)
      )

      val detailState = awaitItem()
      assertThat(detailState).isEqualTo(
        GroupDetailUiState.Detail(
          group = group,
          isWishlistsByGroupsModalOpen = false,
          isSecretSantaEventsByGroupsModalOpen = false,
          wishlistsByGroup = emptyList(),
          secretSantaEventsByGroup = emptyList(),
          isLoading = false,
          error = null,
        )
      )
    }
  }

  @Test
  fun `fetch group when initializing and show error when fetch fails`() = runTest {
    whenever(fetchGroupUseCase(groupId)).thenReturn(Result.failure(RuntimeException()))

    viewModel.uiState.test {
      val loadingState = awaitItem()
      assertThat(loadingState).isEqualTo(
        GroupDetailUiState.Loading(groupName)
      )

      val errorState = awaitItem()
      assertThat(errorState).isEqualTo(
        GroupDetailUiState.Error(groupName)
      )
    }
  }

  @Test
  fun `fetch group again when group is updated`() = runTest {
    val group = detailGroup(id = groupId, name = groupName)
    whenever(fetchGroupUseCase(groupId)).thenReturn(
      Result.success(group),
      Result.success(group),
    )

    viewModel.uiState.test {

      viewModel.onGroupUpdated()

      val loadingState = awaitItem()
      assertThat(loadingState).isEqualTo(
        GroupDetailUiState.Loading(groupName)
      )

      val detailState = awaitItem()
      assertThat(detailState).isEqualTo(
        GroupDetailUiState.Detail(
          group = group,
          isWishlistsByGroupsModalOpen = false,
          isSecretSantaEventsByGroupsModalOpen = false,
          wishlistsByGroup = emptyList(),
          secretSantaEventsByGroup = emptyList(),
          isLoading = false,
          error = null,
        )
      )

      verify(fetchGroupUseCase, AtMost(2)).invoke(groupId)
    }
  }

  @Test
  fun `leave group and emit group updated side effect when update succeeds`() = runTest {
    val group = detailGroup(id = groupId, name = groupName)
    val fullGroup = fullGroup(
      id = groupId,
      name = groupName,
      membersUid = listOf("u1", "u2"),
      photoUrl = "photo"
    )

    whenever(fetchGroupUseCase(groupId)).thenReturn(Result.success(group))
    whenever(updateGroupUseCase(any())).thenReturn(Result.success(Unit))

    turbineScope {
      val uiStateTurbine = viewModel.uiState.testIn(backgroundScope)
      val sideEffectTurbine = viewModel.uiSideEffect.testIn(backgroundScope)

      uiStateTurbine.awaitItem()
      uiStateTurbine.awaitItem()

      viewModel.onLeaveGroup(fullGroup)

      val loadingState = uiStateTurbine.awaitItem()
      assertThat(loadingState).isEqualTo(
        GroupDetailUiState.Detail(
          group = group,
          isWishlistsByGroupsModalOpen = false,
          isSecretSantaEventsByGroupsModalOpen = false,
          wishlistsByGroup = emptyList(),
          secretSantaEventsByGroup = emptyList(),
          isLoading = true,
          error = null,
        )
      )

      val notLoadingState = uiStateTurbine.awaitItem()
      assertThat(notLoadingState).isEqualTo(
        GroupDetailUiState.Detail(
          group = group,
          isWishlistsByGroupsModalOpen = false,
          isSecretSantaEventsByGroupsModalOpen = false,
          wishlistsByGroup = emptyList(),
          secretSantaEventsByGroup = emptyList(),
          isLoading = false,
          error = null,
        )
      )

      val effect = sideEffectTurbine.awaitItem()
      assertThat(effect).isEqualTo(GroupDetailUiSideEffect.GroupUpdated)

      verify(updateGroupUseCase).invoke(
        UpdateGroupRequest(
          id = groupId,
          name = groupName,
          members = listOf("u1", "u2"),
          image = ImageMediaRequest.Url("photo"),
          includeCurrentUser = false,
        )
      )

      uiStateTurbine.cancelAndIgnoreRemainingEvents()
      sideEffectTurbine.cancelAndIgnoreRemainingEvents()
    }
  }

  @Test
  fun `show error when leave group fails`() = runTest {
    val error = RuntimeException()
    val group = detailGroup(id = groupId, name = groupName)
    val fullGroup = fullGroup(
      id = groupId,
      name = groupName,
      membersUid = listOf("u1", "u2"),
      photoUrl = "photo"
    )

    whenever(fetchGroupUseCase(groupId)).thenReturn(Result.success(group))
    whenever(updateGroupUseCase(any())).thenReturn(Result.failure(error))
    whenever(errorUiMapper.map(error)).thenReturn(errorUiModel())

    viewModel.uiState.test {
      awaitItem()
      awaitItem()

      viewModel.onLeaveGroup(fullGroup)

      val loadingState = awaitItem()
      assertThat(loadingState).isEqualTo(
        GroupDetailUiState.Detail(
          group = group,
          isWishlistsByGroupsModalOpen = false,
          isSecretSantaEventsByGroupsModalOpen = false,
          wishlistsByGroup = emptyList(),
          secretSantaEventsByGroup = emptyList(),
          isLoading = true,
          error = null,
        )
      )

      val errorState = awaitItem()
      assertThat(errorState).isEqualTo(
        GroupDetailUiState.Detail(
          group = group,
          isWishlistsByGroupsModalOpen = false,
          isSecretSantaEventsByGroupsModalOpen = false,
          wishlistsByGroup = emptyList(),
          secretSantaEventsByGroup = emptyList(),
          isLoading = false,
          error = errorUiModel(),
        )
      )
    }
  }

  @Test
  fun `dismiss current error`() = runTest {
    val error = RuntimeException()
    val group = detailGroup(id = groupId, name = groupName)
    val fullGroup = fullGroup(
      id = groupId,
      name = groupName,
      membersUid = listOf("u1", "u2"),
      photoUrl = "photo"
    )

    whenever(fetchGroupUseCase(groupId)).thenReturn(Result.success(group))
    whenever(updateGroupUseCase(any())).thenReturn(Result.failure(error))
    whenever(errorUiMapper.map(error)).thenReturn(errorUiModel())

    viewModel.uiState.test {
      awaitItem()
      awaitItem()

      viewModel.onLeaveGroup(fullGroup)
      awaitItem()
      awaitItem()

      viewModel.onDismissError()

      val uiState = awaitItem()
      assertThat(uiState).isEqualTo(
        GroupDetailUiState.Detail(
          group = group,
          isWishlistsByGroupsModalOpen = false,
          isSecretSantaEventsByGroupsModalOpen = false,
          wishlistsByGroup = emptyList(),
          secretSantaEventsByGroup = emptyList(),
          isLoading = false,
          error = null,
        )
      )
    }
  }

  private fun detailGroup(
    id: String,
    name: String,
  ) = Group.Detail(
    id = id,
    name = name,
    photoUrl = null,
    members = emptyList(),
    currentUserUid = "",
    hasSharedWishlists = false,
    hasSecretSantaEvents = false
  )

  private fun fullGroup(
    id: String,
    name: String,
    membersUid: List<String>,
    photoUrl: String?,
  ) = Group.Detail(
    id = id,
    name = name,
    photoUrl = photoUrl,
    members = membersUid.map { User.Basic(uid = it, username = "", code = "", photoUrl = null) },
    currentUserUid = "",
    hasSecretSantaEvents = false,
    hasSharedWishlists = false
  )
}