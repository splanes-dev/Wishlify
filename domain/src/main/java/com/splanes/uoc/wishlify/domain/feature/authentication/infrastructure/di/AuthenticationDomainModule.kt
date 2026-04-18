package com.splanes.uoc.wishlify.domain.feature.authentication.infrastructure.di

import com.splanes.uoc.wishlify.domain.feature.authentication.usecase.AutoSignInUseCase
import com.splanes.uoc.wishlify.domain.feature.authentication.usecase.GoogleSignInUseCase
import com.splanes.uoc.wishlify.domain.feature.authentication.usecase.GoogleSignUpUseCase
import com.splanes.uoc.wishlify.domain.feature.authentication.usecase.SignInUseCase
import com.splanes.uoc.wishlify.domain.feature.authentication.usecase.SignOutUseCase
import com.splanes.uoc.wishlify.domain.feature.authentication.usecase.SignUpUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val AuthenticationDomainModule = module {
  // Use cases
  singleOf(::SignUpUseCase)
  singleOf(::AutoSignInUseCase)
  singleOf(::SignInUseCase)
  singleOf(::GoogleSignUpUseCase)
  singleOf(::GoogleSignInUseCase)
  singleOf(::SignOutUseCase)
}