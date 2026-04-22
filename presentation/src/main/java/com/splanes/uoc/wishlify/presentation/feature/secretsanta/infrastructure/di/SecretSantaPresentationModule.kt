package com.splanes.uoc.wishlify.presentation.feature.secretsanta.infrastructure.di

import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.chat.SecretSantaChatViewModel
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.chat.model.SecretSantaChatType
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.detail.SecretSantaDetailViewModel
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.hobbies.SecretSantaHobbiesViewModel
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.SecretSantaListViewModel
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.creation.SecretSantaNewEventViewModel
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.creation.mapper.SecretSantaNewEventFormErrorMapper
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.creation.mapper.SecretSantaNewEventFormMapper
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.edition.SecretSantaUpdateEventViewModel
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.share.SecretSantaShareWishlistViewModel
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.wishlist.SecretSantaWishlistViewModel
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.infrastructure.navigation.SecretSantaExternalActionHandler
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.infrastructure.navigation.SecretSantaNavGraph
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.FeatureHomeNavGraph
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val SecretSantaPresentationModule = module {
  // Navigation
  singleOf(::SecretSantaNavGraph) bind FeatureHomeNavGraph::class
  singleOf(::SecretSantaExternalActionHandler)

  // ViewModels
  viewModelOf(::SecretSantaListViewModel)
  viewModelOf(::SecretSantaNewEventViewModel)
  viewModel { (eventId: String) ->
    SecretSantaUpdateEventViewModel(
      eventId = eventId,
      fetchSecretSantaDetailUseCase = get(),
      fetchGroupsUseCase = get(),
      fetchUserByIdUseCase = get(),
      validateSecretSantaDrawUseCase = get(),
      updateSecretSantaEventUseCase = get(),
      formMapper = get(),
      formErrorMapper = get(),
      errorUiMapper = get(),
    )
  }
  viewModel { (eventId: String, name: String) ->
    SecretSantaDetailViewModel(
      eventId = eventId,
      eventName = name,
      fetchSecretSantaDetailUseCase = get(),
      doSecretSantaDrawUseCase = get(),
      errorUiMapper = get(),
    )
  }
  viewModel { (eventId: String, wishlistOwnerId: String?, isOwnWishlist: Boolean) ->
    SecretSantaWishlistViewModel(
      eventId = eventId,
      wishlistOwnerId = wishlistOwnerId,
      isOwnWishlist = isOwnWishlist,
      fetchSecretSantaWishlistUseCase = get(),
      fetchSecretSantaWishlistItemsUseCase = get(),
      unshareWishlistSecretSantaUseCase = get(),
      errorUiMapper = get()
    )
  }
  viewModel { (eventId: String) ->
    SecretSantaShareWishlistViewModel(
      eventId = eventId,
      fetchWishlistsUseCase = get(),
      fetchWishlistItemsUseCase = get(),
      shareWishlistSecretSantaUseCase = get(),
      errorUiMapper = get()
    )
  }
  viewModel { (eventId: String, type: String, otherUid: String) ->
    SecretSantaChatViewModel(
      eventId = eventId,
      chatType = SecretSantaChatType.from(type),
      otherUid = otherUid,
      fetchUserByIdUseCase = get(),
      subscribeSecretSantaChatUseCase = get(),
      fetchSecretSantaChatMessagesUseCase = get(),
      sendMessageSecretSantaChatUseCase = get()
    )
  }
  viewModel { (targetUid: String) ->
    SecretSantaHobbiesViewModel(
      targetUid = targetUid,
      fetchUserHobbiesUseCase = get(),
      errorUiMapper = get()
    )
  }

  // Mappers
  singleOf(::SecretSantaNewEventFormErrorMapper)
  singleOf(::SecretSantaNewEventFormMapper)
}