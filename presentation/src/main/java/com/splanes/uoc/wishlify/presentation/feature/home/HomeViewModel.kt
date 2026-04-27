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

/**
 * ViewModel that drives the app home shell.
 *
 * It observes session expiration and translates incoming external share or
 * deeplink actions into navigation side effects for downstream features.
 */
class HomeViewModel(
  private val getSessionStateFlowUseCase: GetSessionStateFlowUseCase,
  private val deeplinkMapper: DeeplinkMapper,
) : ViewModel() {

  private val uiSideEffectChannel = Channel<HomeUiSideEffect>()
  val uiSideEffect = uiSideEffectChannel.receiveAsFlow()

  private var observeJob: Job? = null

  /** Starts observing session state changes and emits logout navigation when needed. */
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

  /** Translates incoming Android share actions into wishlist-item creation flows. */
  fun onCreateWishlistItemByShare(action: CreateNewWishlistItem) {
    val effect = when (action) {
      is NewWishlistItemByImage -> HomeUiSideEffect.NavToWishlistNewItemByUri(action.uri.toString())
      is NewWishlistItemByUrl -> HomeUiSideEffect.NavToWishlistNewItemByUrl(action.url)
    }
    viewModelScope.launch { uiSideEffectChannel.send(effect) }
  }

  /** Resolves a deeplink URI into the matching feature navigation side effect. */
  fun onOpenDeeplink(uri: Uri) {
    val effect = when (val deeplink = deeplinkMapper.map(uri)) {
      is Deeplink.SecretSanta -> HomeUiSideEffect.NavToSecretSanta(deeplink)
      is Deeplink.SharedWishlist -> HomeUiSideEffect.NavToSharedWishlist(deeplink)
      is Deeplink.JoinWishlistEditor -> HomeUiSideEffect.NavToWishlist(deeplink)
      null -> null
    }
    effect?.let {
      viewModelScope.launch { uiSideEffectChannel.send(effect) }
    }
  }

  /** Stops the session-state observer when the home shell no longer needs it. */
  fun cancelSessionStateObserver() {
    observeJob?.cancel()
  }
}
