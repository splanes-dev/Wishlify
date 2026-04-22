package com.splanes.uoc.wishlify.presentation.feature.shared.infrastructure.di

import com.splanes.uoc.wishlify.presentation.feature.shared.feature.chat.SharedWishlistThirdPartyChatViewModel
import com.splanes.uoc.wishlify.presentation.feature.shared.feature.detail.SharedWishlistThirdPartyDetailViewModel
import com.splanes.uoc.wishlify.presentation.feature.shared.feature.detail.mapper.SharedWishlistItemStateErrorMapper
import com.splanes.uoc.wishlify.presentation.feature.shared.feature.detail.mapper.SharedWishlistItemUiMapper
import com.splanes.uoc.wishlify.presentation.feature.shared.feature.list.SharedWishlistsListViewModel
import com.splanes.uoc.wishlify.presentation.feature.shared.infrastructure.navigation.SharedWishlistExternalActionHandler
import com.splanes.uoc.wishlify.presentation.feature.shared.infrastructure.navigation.SharedWishlistsNavGraph
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.FeatureHomeNavGraph
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val SharedWishlistsPresentationModule = module {
  // Navigation
  singleOf(::SharedWishlistsNavGraph) bind FeatureHomeNavGraph::class
  singleOf(::SharedWishlistExternalActionHandler)

  // ViewModels
  viewModelOf(::SharedWishlistsListViewModel)
  viewModel { (wishlistId: String, wishlistName: String, target: String) ->
    SharedWishlistThirdPartyDetailViewModel(
      sharedWishlistId = wishlistId,
      sharedWishlistName = wishlistName,
      target = target,
      fetchSharedWishlistUseCase = get(),
      fetchSharedWishlistItemsUseCase = get(),
      fetchSharedWishlistItemUseCase = get(),
      updateSharedWishlistItemUseCase = get(),
      itemUiMapper = get(),
      itemStateErrorMapper = get(),
      errorUiMapper = get()
    )
  }
  viewModel { (wishlistId: String, wishlistName: String, target: String) ->
    SharedWishlistThirdPartyChatViewModel(
      sharedWishlistId = wishlistId,
      sharedWishlistName = wishlistName,
      target = target,
      sendMessageSharedWishlistChatUseCase = get(),
      subscribeSharedWishlistChatUseCase = get(),
      fetchSharedWishlistChatOlderMessagesUseCase = get(),
    )
  }

  // Mappers
  singleOf(::SharedWishlistItemStateErrorMapper)
  singleOf(::SharedWishlistItemUiMapper)
}