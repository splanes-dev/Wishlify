package com.splanes.uoc.wishlify.presentation.feature.groups.feature.detail

import com.splanes.uoc.wishlify.domain.feature.groups.model.Group
import com.splanes.uoc.wishlify.domain.feature.secretsanta.model.SecretSantaEvent
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlist
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiModel

sealed interface GroupDetailUiState {

  data class Loading(val groupName: String) : GroupDetailUiState
  data class Error(val groupName: String) : GroupDetailUiState
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

sealed interface GroupDetailUiSideEffect {
  data object GroupUpdated : GroupDetailUiSideEffect
}