package com.splanes.uoc.wishlify.domain.feature.secresanta.infrastructure.di

import com.splanes.uoc.wishlify.domain.feature.secresanta.helper.SecretSantaChatIdBuilder
import com.splanes.uoc.wishlify.domain.feature.secresanta.helper.SecretSantaDrawExecutor
import com.splanes.uoc.wishlify.domain.feature.secresanta.usecase.CreateSecretSantaEventUseCase
import com.splanes.uoc.wishlify.domain.feature.secresanta.usecase.DoSecretSantaDrawUseCase
import com.splanes.uoc.wishlify.domain.feature.secresanta.usecase.FetchSecretSantaChatMessagesUseCase
import com.splanes.uoc.wishlify.domain.feature.secresanta.usecase.FetchSecretSantaDetailUseCase
import com.splanes.uoc.wishlify.domain.feature.secresanta.usecase.FetchSecretSantaEventsUseCase
import com.splanes.uoc.wishlify.domain.feature.secresanta.usecase.FetchSecretSantaWishlistItemsUseCase
import com.splanes.uoc.wishlify.domain.feature.secresanta.usecase.FetchSecretSantaWishlistUseCase
import com.splanes.uoc.wishlify.domain.feature.secresanta.usecase.SendMessageSecretSantaChatUseCase
import com.splanes.uoc.wishlify.domain.feature.secresanta.usecase.ShareWishlistSecretSantaUseCase
import com.splanes.uoc.wishlify.domain.feature.secresanta.usecase.SubscribeSecretSantaChatUseCase
import com.splanes.uoc.wishlify.domain.feature.secresanta.usecase.UnshareWishlistSecretSantaUseCase
import com.splanes.uoc.wishlify.domain.feature.secresanta.usecase.UpdateSecretSantaEventUseCase
import com.splanes.uoc.wishlify.domain.feature.secresanta.usecase.ValidateSecretSantaDrawUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val SecretSantaDomainModule = module {
  // Utils
  singleOf(::SecretSantaDrawExecutor)
  singleOf(::SecretSantaChatIdBuilder)
  // Use cases
  singleOf(::FetchSecretSantaEventsUseCase)
  singleOf(::FetchSecretSantaDetailUseCase)
  singleOf(::ValidateSecretSantaDrawUseCase)
  singleOf(::CreateSecretSantaEventUseCase)
  singleOf(::UpdateSecretSantaEventUseCase)
  singleOf(::DoSecretSantaDrawUseCase)
  singleOf(::ShareWishlistSecretSantaUseCase)
  singleOf(::UnshareWishlistSecretSantaUseCase)
  singleOf(::FetchSecretSantaWishlistUseCase)
  singleOf(::FetchSecretSantaWishlistItemsUseCase)
  singleOf(::FetchSecretSantaChatMessagesUseCase)
  singleOf(::SubscribeSecretSantaChatUseCase)
  singleOf(::SendMessageSecretSantaChatUseCase)
}