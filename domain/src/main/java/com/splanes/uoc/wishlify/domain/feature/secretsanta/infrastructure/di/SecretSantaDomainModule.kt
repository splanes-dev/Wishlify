package com.splanes.uoc.wishlify.domain.feature.secretsanta.infrastructure.di

import com.splanes.uoc.wishlify.domain.feature.secretsanta.helper.SecretSantaChatIdBuilder
import com.splanes.uoc.wishlify.domain.feature.secretsanta.helper.SecretSantaDrawExecutor
import com.splanes.uoc.wishlify.domain.feature.secretsanta.usecase.AddEventParticipantFromLinkUseCase
import com.splanes.uoc.wishlify.domain.feature.secretsanta.usecase.CreateSecretSantaEventUseCase
import com.splanes.uoc.wishlify.domain.feature.secretsanta.usecase.DoSecretSantaDrawUseCase
import com.splanes.uoc.wishlify.domain.feature.secretsanta.usecase.FetchSecretSantaChatMessagesUseCase
import com.splanes.uoc.wishlify.domain.feature.secretsanta.usecase.FetchSecretSantaDetailUseCase
import com.splanes.uoc.wishlify.domain.feature.secretsanta.usecase.FetchSecretSantaEventsUseCase
import com.splanes.uoc.wishlify.domain.feature.secretsanta.usecase.FetchSecretSantaWishlistItemsUseCase
import com.splanes.uoc.wishlify.domain.feature.secretsanta.usecase.FetchSecretSantaWishlistUseCase
import com.splanes.uoc.wishlify.domain.feature.secretsanta.usecase.SendMessageSecretSantaChatUseCase
import com.splanes.uoc.wishlify.domain.feature.secretsanta.usecase.ShareWishlistSecretSantaUseCase
import com.splanes.uoc.wishlify.domain.feature.secretsanta.usecase.SubscribeSecretSantaChatUseCase
import com.splanes.uoc.wishlify.domain.feature.secretsanta.usecase.UnshareWishlistSecretSantaUseCase
import com.splanes.uoc.wishlify.domain.feature.secretsanta.usecase.UpdateSecretSantaEventUseCase
import com.splanes.uoc.wishlify.domain.feature.secretsanta.usecase.ValidateSecretSantaDrawUseCase
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
  singleOf(::AddEventParticipantFromLinkUseCase)
}