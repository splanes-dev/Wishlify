package com.splanes.uoc.wishlify.domain.feature.authentication.infrastructure.di

import com.splanes.uoc.wishlify.domain.feature.authentication.usecase.SignUpUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val AuthenticationDomainModule = module {
  // Use cases
  singleOf(::SignUpUseCase)
}