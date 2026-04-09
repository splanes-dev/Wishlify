package com.splanes.uoc.wishlify.presentation.feature.shared.feature.detail.thirdparty.model

sealed interface SharedWishlistItemAction{
  data object Open : SharedWishlistItemAction
  data object OpenLink : SharedWishlistItemAction
  sealed interface UpdateState : SharedWishlistItemAction

  data object Purchase : UpdateState
  data object Lock : UpdateState
  data class ShareRequest(val numOfParticipants: Int) : UpdateState
  data object Unlock : UpdateState
  data object JoinToShareRequest : UpdateState
  data object CancelShareRequest : UpdateState
}