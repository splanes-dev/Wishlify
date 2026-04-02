package com.splanes.uoc.wishlify.presentation.feature.wishlists.infrastructure.di

import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.WishlistsListViewModel
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.creation.WishlistsNewListViewModel
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.creation.mapper.WishlistFormErrorMapper
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.creation.mapper.WishlistFormUiMapper
import com.splanes.uoc.wishlify.presentation.feature.wishlists.infrastructure.navigation.Wishlists
import com.splanes.uoc.wishlify.presentation.feature.wishlists.infrastructure.navigation.WishlistsNavGraph
import com.splanes.uoc.wishlify.presentation.feature.wishlists.mapper.CategoryFormErrorMapper
import com.splanes.uoc.wishlify.presentation.feature.wishlists.mapper.CategoryUiMapper
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.FeatureHomeNavGraph
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.HomeNavStartRoute
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

internal val WishlistsPresentationModule = module {
  // Navigation
  single(named(HomeNavStartRoute)) { Wishlists } bind Any::class
  singleOf(::WishlistsNavGraph) bind FeatureHomeNavGraph::class
  // ViewModels
  viewModelOf(::WishlistsListViewModel)
  viewModel { (isOwnWishlist: Boolean) ->
    WishlistsNewListViewModel(
      isOwnWishlist = isOwnWishlist,
      fetchCategoriesUseCase = get(),
      createWishlistUseCase = get(),
      createCategoryUseCase = get(),
      wishlistFormUiMapper = get(),
      wishlistFormErrorMapper = get(),
      categoryFormErrorMapper = get(),
      categoryUiMapper = get(),
      errorUiMapper = get(),
    )
  }
  // Mappers
  singleOf(::CategoryUiMapper)
  singleOf(::CategoryFormErrorMapper)
  singleOf(::WishlistFormErrorMapper)
  singleOf(::WishlistFormUiMapper)
}