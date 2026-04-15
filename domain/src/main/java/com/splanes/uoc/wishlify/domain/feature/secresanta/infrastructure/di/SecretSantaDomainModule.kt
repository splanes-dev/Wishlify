package com.splanes.uoc.wishlify.domain.feature.secresanta.infrastructure.di

import com.splanes.uoc.wishlify.domain.feature.secresanta.helper.SecretSantaDrawExecutor
import com.splanes.uoc.wishlify.domain.feature.secresanta.usecase.CreateSecretSantaEventUseCase
import com.splanes.uoc.wishlify.domain.feature.secresanta.usecase.DoSecretSantaDrawUseCase
import com.splanes.uoc.wishlify.domain.feature.secresanta.usecase.FetchSecretSantaDetailUseCase
import com.splanes.uoc.wishlify.domain.feature.secresanta.usecase.FetchSecretSantaEventsUseCase
import com.splanes.uoc.wishlify.domain.feature.secresanta.usecase.ShareWishlistSecretSantaUseCase
import com.splanes.uoc.wishlify.domain.feature.secresanta.usecase.UpdateSecretSantaEventUseCase
import com.splanes.uoc.wishlify.domain.feature.secresanta.usecase.ValidateSecretSantaDrawUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val SecretSantaDomainModule = module {
  // Utils
  singleOf(::SecretSantaDrawExecutor)
  // Use cases
  singleOf(::FetchSecretSantaEventsUseCase)
  singleOf(::FetchSecretSantaDetailUseCase)
  singleOf(::ValidateSecretSantaDrawUseCase)
  singleOf(::CreateSecretSantaEventUseCase)
  singleOf(::UpdateSecretSantaEventUseCase)
  singleOf(::DoSecretSantaDrawUseCase)
  singleOf(::ShareWishlistSecretSantaUseCase)
}