package com.splanes.uoc.wishlify.domain.feature.wishlists.infrastructure.di

import com.splanes.uoc.wishlify.domain.feature.wishlists.usecase.CreateCategoryUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.usecase.CreateWishlistItemUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.usecase.CreateWishlistUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.usecase.DeleteWishlistItemUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.usecase.FetchCategoriesUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.usecase.FetchWishlistItemUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.usecase.FetchWishlistItemsUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.usecase.FetchWishlistUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.usecase.FetchWishlistsUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.usecase.UpdateWishlistItemPurchaseUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.usecase.UpdateWishlistItemUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val WishlistsDomainModule = module {
  // Use cases
  singleOf(::FetchWishlistsUseCase)
  singleOf(::FetchWishlistUseCase)
  singleOf(::FetchWishlistItemsUseCase)
  singleOf(::FetchWishlistItemUseCase)
  singleOf(::FetchCategoriesUseCase)
  singleOf(::CreateCategoryUseCase)
  singleOf(::CreateWishlistUseCase)
  singleOf(::CreateWishlistItemUseCase)
  singleOf(::DeleteWishlistItemUseCase)
  singleOf(::UpdateWishlistItemUseCase)
  singleOf(::UpdateWishlistItemPurchaseUseCase)
}