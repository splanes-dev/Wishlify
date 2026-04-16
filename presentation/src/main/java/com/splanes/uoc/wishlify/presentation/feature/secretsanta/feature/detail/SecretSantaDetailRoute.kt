package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SecretSantaDetailRoute(
  viewModel: SecretSantaDetailViewModel,
  onNavToEdit: (eventId: String) -> Unit,
  onNavToWishlist: (eventId: String, wishlistOwnerId: String?, isOwnWishlist: Boolean) -> Unit,
  onNavToShareWishlist: (eventId: String) -> Unit,
  onNavToChat: (eventId: String, type: String, otherUid: String) -> Unit,
  onNavBack: (update: Boolean) -> Unit,
) {

  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect(Unit) {
    viewModel.uiSideEffect.collect { effect ->
      when (effect) {
        is SecretSantaDetailUiSideEffect.NavToEdit ->
          onNavToEdit(effect.event)

        is SecretSantaDetailUiSideEffect.NavToShareWishlist ->
          onNavToShareWishlist(effect.event)

        is SecretSantaDetailUiSideEffect.NavToWishlist ->
          onNavToWishlist(
            effect.eventId,
            effect.wishlistOwnerId,
            effect.isOwnWishlist
          )

        is SecretSantaDetailUiSideEffect.NavToChat ->
          onNavToChat(
            effect.eventId,
            effect.chatType,
            effect.otherUid
          )
      }
    }
  }

  when (val state = uiState) {
    is SecretSantaDetailUiState.Detail ->
      SecretSantaDetailScreen(
        uiState = state,
        onAction = viewModel::onAction,
        onDismissError = viewModel::onDismissError,
        onBack = { onNavBack(false) }
      )

    is SecretSantaDetailUiState.Error ->
      SecretSantaDetailErrorScreen(
        uiState = state,
        onBack = { onNavBack(false) }
      )

    is SecretSantaDetailUiState.Loading ->
      SecretSantaDetailLoadingScreen(
        uiState = state,
        onBack = { onNavBack(false) }
      )

  }
}