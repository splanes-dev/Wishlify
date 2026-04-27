package com.splanes.uoc.wishlify.presentation.feature.groups.feature.detail

import com.splanes.uoc.wishlify.domain.feature.groups.model.Group
import com.splanes.uoc.wishlify.domain.feature.secretsanta.model.SecretSantaEvent
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlist
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiModel

/** UI states rendered by the group detail flow. */
sealed interface GroupDetailUiState {

  /** Fullscreen loading state used while the group detail is being resolved. */
  data class Loading(val groupName: String) : GroupDetailUiState
  /** Fullscreen error state used when the requested group cannot be resolved. */
  data class Error(val groupName: String) : GroupDetailUiState
  /** Detailed state of the group, including related shared wishlists and events. */
  data class Detail(
    val group: Group.Detail,
    val isWishlistsByGroupsModalOpen: Boolean,
    val isSecretSantaEventsByGroupsModalOpen: Boolean,
    val wishlistsByGroup: List<SharedWishlist>,
    val secretSantaEventsByGroup: List<SecretSantaEvent>,
    val isLoading: Boolean,
    val error: ErrorUiModel?
  ): GroupDetailUiState
}

/** One-off effects emitted by the group detail flow. */
sealed interface GroupDetailUiSideEffect {
  /** Requests the parent flow to refresh after the group has been updated. */
  data object GroupUpdated : GroupDetailUiSideEffect
}
