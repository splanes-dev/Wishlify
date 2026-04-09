package com.splanes.uoc.wishlify.domain.feature.shared.infrastructure.di

import com.splanes.uoc.wishlify.domain.feature.shared.usecase.FetchSharedWishlistItemUseCase
import com.splanes.uoc.wishlify.domain.feature.shared.usecase.FetchSharedWishlistItemsUseCase
import com.splanes.uoc.wishlify.domain.feature.shared.usecase.FetchSharedWishlistUseCase
import com.splanes.uoc.wishlify.domain.feature.shared.usecase.FetchSharedWishlistsUseCase
import com.splanes.uoc.wishlify.domain.feature.shared.usecase.UpdateSharedWishlistItemUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val SharedWishlistsDomainModule = module {
   // Use case
  singleOf(::FetchSharedWishlistsUseCase)
  singleOf(::FetchSharedWishlistUseCase)
  singleOf(::FetchSharedWishlistItemsUseCase)
  singleOf(::FetchSharedWishlistItemUseCase)
  singleOf(::UpdateSharedWishlistItemUseCase)
}