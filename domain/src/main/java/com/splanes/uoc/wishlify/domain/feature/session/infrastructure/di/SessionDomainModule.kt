package com.splanes.uoc.wishlify.domain.feature.session.infrastructure.di

import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetCurrentUserIdUseCase
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetSessionStateFlowUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val SessionDomainModule = module {
  // Use cases
  singleOf(::GetSessionStateFlowUseCase)
  singleOf(::GetCurrentUserIdUseCase)
}