package com.splanes.uoc.wishlify.domain.feature.wishlists.infrastructure.di

import com.splanes.uoc.wishlify.domain.feature.wishlists.usecase.CreateCategoryUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.usecase.CreateWishlistUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.usecase.FetchCategoriesUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val WishlistsDomainModule = module {
  // Use cases
  singleOf(::FetchCategoriesUseCase)
  singleOf(::CreateCategoryUseCase)
  singleOf(::CreateWishlistUseCase)
}