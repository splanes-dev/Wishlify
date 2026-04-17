package com.splanes.uoc.wishlify.presentation.feature.groups.infrastructure.di

import com.splanes.uoc.wishlify.presentation.feature.groups.feature.detail.GroupDetailViewModel
import com.splanes.uoc.wishlify.presentation.feature.groups.feature.list.GroupsListViewModel
import com.splanes.uoc.wishlify.presentation.feature.groups.feature.list.creation.GroupsNewGroupViewModel
import com.splanes.uoc.wishlify.presentation.feature.groups.feature.list.creation.mapper.GroupsNewGroupFormErrorMapper
import com.splanes.uoc.wishlify.presentation.feature.groups.feature.list.creation.mapper.GroupsNewGroupFormMapper
import com.splanes.uoc.wishlify.presentation.feature.groups.feature.list.edition.GroupsEditGroupViewModel
import com.splanes.uoc.wishlify.presentation.feature.groups.feature.search.GroupsSearchUsersViewModel
import com.splanes.uoc.wishlify.presentation.feature.groups.infrastructure.navigation.GroupsNavGraph
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.FeatureHomeNavGraph
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val GroupsPresentationModule = module {
  // Navigation
  singleOf(::GroupsNavGraph) bind FeatureHomeNavGraph::class

  // ViewModels
  viewModelOf(::GroupsListViewModel)
  viewModelOf(::GroupsNewGroupViewModel)
  viewModelOf(::GroupsSearchUsersViewModel)
  viewModel { (groupId: String, groupName: String) ->
    GroupDetailViewModel(
      groupId = groupId,
      groupName = groupName,
      fetchGroupUseCase = get(),
      updateGroupUseCase = get(),
      errorUiMapper = get()
    )
  }
  viewModel { (groupId: String, groupName: String) ->
    GroupsEditGroupViewModel(
      groupId = groupId,
      groupName = groupName,
      fetchGroupUseCase = get(),
      fetchUserByIdUseCase = get(),
      updateGroupUseCase = get(),
      formMapper = get(),
      formErrorsMapper = get(),
      errorUiMapper = get()
    )
  }

  // Mappers
  singleOf(::GroupsNewGroupFormMapper)
  singleOf(::GroupsNewGroupFormErrorMapper)
}