package com.splanes.uoc.wishlify.presentation.feature.groups.feature.search

import com.splanes.uoc.wishlify.domain.feature.user.model.User
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiModel

/** UI state rendered by the user-search flow used to add group members. */
data class GroupsSearchUsersUiState(
  val searchQuery: String,
  val results: List<User.Basic>,
  val added: List<User.Basic>,
  val isInfoBannerVisible: Boolean,
  val isLoading: Boolean,
  val error: ErrorUiModel?
)
