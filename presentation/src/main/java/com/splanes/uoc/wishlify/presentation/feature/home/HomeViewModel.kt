package com.splanes.uoc.wishlify.presentation.feature.home

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.splanes.uoc.wishlify.domain.feature.session.model.SessionState
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetSessionStateFlowUseCase
import com.splanes.uoc.wishlify.presentation.common.deeplink.Deeplink
import com.splanes.uoc.wishlify.presentation.common.deeplink.DeeplinkMapper
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.actions.CreateNewWishlistItem
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.actions.NewWishlistItemByImage
import com.splanes.uoc.wishlify.presentation.infrastructure.navigation.actions.NewWishlistItemByUrl
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class HomeViewModel(
  private val getSessionStateFlowUseCase: GetSessionStateFlowUseCase,
  private val deeplinkMapper: DeeplinkMapper,
) : ViewModel() {

  private val uiSideEffectChannel = Channel<HomeUiSideEffect>()
  val uiSideEffect = uiSideEffectChannel.receiveAsFlow()

  private var observeJob: Job? = null

  fun observeSessionState() {
    if (observeJob?.isActive == true) return

    observeJob = viewModelScope.launch {
      getSessionStateFlowUseCase().collect { state ->
        if (state == SessionState.SignedOut) {
          uiSideEffectChannel.send(HomeUiSideEffect.NoSession)
        }
      }
    }
  }

  fun onCreateWishlistItemByShare(action: CreateNewWishlistItem) {
    val uri = when (action) {
      is NewWishlistItemByImage -> action.uri.toString()
      is NewWishlistItemByUrl -> action.url
    }
    uiSideEffectChannel.trySend(HomeUiSideEffect.NavToWishlistNewItem(uri))
  }

  fun onOpenDeeplink(uri: Uri) {
    val effect = when (val deeplink = deeplinkMapper.map(uri)) {
      is Deeplink.SecretSanta -> HomeUiSideEffect.NavToSecretSanta(deeplink)
      is Deeplink.WishlistEditor -> HomeUiSideEffect.NavToWishlist(deeplink)
      is Deeplink.WishlistShare -> HomeUiSideEffect.NavToSharedWishlist(deeplink)
      null -> null
    }

    effect?.let(uiSideEffectChannel::trySend)
  }

  fun cancelSessionStateObserver() {
    observeJob?.cancel()
  }
}