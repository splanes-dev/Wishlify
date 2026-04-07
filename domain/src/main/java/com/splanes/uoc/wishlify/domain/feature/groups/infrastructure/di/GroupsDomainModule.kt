package com.splanes.uoc.wishlify.domain.feature.groups.infrastructure.di

import com.splanes.uoc.wishlify.domain.feature.groups.usecase.CreateGroupUseCase
import com.splanes.uoc.wishlify.domain.feature.groups.usecase.FetchGroupsUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val GroupsDomainModule = module {
  // Use cases
  singleOf(::FetchGroupsUseCase)
  singleOf(::CreateGroupUseCase)
}