package com.splanes.uoc.wishlify.presentation.feature.profile.infrastructure.di

import com.splanes.uoc.wishlify.presentation.feature.profile.feature.hobbies.ProfileHobbiesViewModel
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.main.ProfileMainViewModel
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.notifications.ProfileNotificationsViewModel
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.password.ProfileUpdatePasswordViewModel
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.password.mapper.UserProfileUpdatePasswordFormErrorUiMapper
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.password.mapper.UserProfileUpdatePasswordFormMapper
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.update.ProfileUpdateViewModel
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.update.mapper.UserProfileUpdateFormErrorUiMapper
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.update.mapper.UserProfileUpdateFormMapper
import com.splanes.uoc.wishlify.presentation.feature.profile.infrastructure.navigation.ProfileNavGraph
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.FeatureHomeNavGraph
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val ProfilePresentationModule = module {
  // Navigation
  singleOf(::ProfileNavGraph) bind FeatureHomeNavGraph::class

  // ViewModels
  viewModelOf(::ProfileMainViewModel)
  viewModelOf(::ProfileUpdateViewModel)
  viewModelOf(::ProfileUpdatePasswordViewModel)
  viewModelOf(::ProfileHobbiesViewModel)
  viewModelOf(::ProfileNotificationsViewModel)

  // Mappers
  singleOf(::UserProfileUpdateFormErrorUiMapper)
  singleOf(::UserProfileUpdateFormMapper)
  singleOf(::UserProfileUpdatePasswordFormErrorUiMapper)
  singleOf(::UserProfileUpdatePasswordFormMapper)
}