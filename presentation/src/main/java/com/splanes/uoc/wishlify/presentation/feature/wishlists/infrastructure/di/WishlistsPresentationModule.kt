package com.splanes.uoc.wishlify.presentation.feature.wishlists.infrastructure.di

import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.WishlistDetailViewModel
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.creation.WishlistNewItemViewModel
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.edition.WishlistEditItemViewModel
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.mapper.WishlistItemFormErrorMapper
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.mapper.WishlistItemFormUiMapper
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.share.WishlistShareViewModel
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.share.mapper.WishlistShareFormUiMapper
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.share.mapper.WishlistShareUiMapper
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.WishlistsListViewModel
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.categories.WishlistsCategoriesViewModel
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
  viewModelOf(::WishlistsCategoriesViewModel)
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
  viewModel { (wishlistId: String, wishlistName: String) ->
    WishlistDetailViewModel(
      wishlistId = wishlistId,
      wishlistName = wishlistName,
      fetchWishlistUseCase = get(),
      fetchWishlistItemsUseCase = get(),
      fetchWishlistItemUseCase = get(),
      deleteWishlistItemUseCase = get(),
      updateWishlistItemPurchaseUseCase = get(),
      errorUiMapper = get(),
    )
  }
  viewModel { (wishlistId: String, link: String?) ->
    WishlistNewItemViewModel(
      wishlistId = wishlistId,
      link = link,
      createWishlistItemUseCase = get(),
      formErrorMapper = get(),
      formUiMapper = get(),
      errorUiMapper = get(),
    )
  }
  viewModel { (wishlistId: String, itemId: String) ->
    WishlistEditItemViewModel(
      wishlistId = wishlistId,
      itemId = itemId,
      fetchWishlistItemUseCase = get(),
      updateWishlistItemUseCase = get(),
      formErrorMapper = get(),
      formUiMapper = get(),
      errorUiMapper = get(),
    )
  }
  viewModel { (wishlistId: String) ->
    WishlistShareViewModel(
      wishlistId = wishlistId,
      fetchWishlistUseCase = get(),
      fetchGroupsUseCase = get(),
      shareWishlistUseCase = get(),
      formUiMapper = get(),
      wishlistShareUiMapper = get(),
      errorUiMapper = get(),
    )
  }
  // Mappers
  singleOf(::CategoryUiMapper)
  singleOf(::CategoryFormErrorMapper)
  singleOf(::WishlistFormErrorMapper)
  singleOf(::WishlistItemFormErrorMapper)
  singleOf(::WishlistFormUiMapper)
  singleOf(::WishlistItemFormUiMapper)
  singleOf(::WishlistShareFormUiMapper)
  singleOf(::WishlistShareUiMapper)
}