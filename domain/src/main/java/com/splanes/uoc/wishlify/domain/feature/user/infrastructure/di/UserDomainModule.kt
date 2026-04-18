package com.splanes.uoc.wishlify.domain.feature.user.infrastructure.di

import com.splanes.uoc.wishlify.domain.feature.user.usecase.FetchBasicUserProfileUseCase
import com.splanes.uoc.wishlify.domain.feature.user.usecase.FetchUserByIdUseCase
import com.splanes.uoc.wishlify.domain.feature.user.usecase.FetchUserHobbiesUseCase
import com.splanes.uoc.wishlify.domain.feature.user.usecase.SearchUserUseCase
import com.splanes.uoc.wishlify.domain.feature.user.usecase.UpdateUserPasswordUseCase
import com.splanes.uoc.wishlify.domain.feature.user.usecase.UpdateUserProfileUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val UserDomainModule = module {
  // Use Cases
  singleOf(::SearchUserUseCase)
  singleOf(::FetchUserByIdUseCase)
  singleOf(::FetchBasicUserProfileUseCase)
  singleOf(::FetchUserHobbiesUseCase)
  singleOf(::UpdateUserPasswordUseCase)
  singleOf(::UpdateUserProfileUseCase)
}