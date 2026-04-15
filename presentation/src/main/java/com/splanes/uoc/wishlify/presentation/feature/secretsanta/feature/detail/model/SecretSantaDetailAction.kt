package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.detail.model

sealed interface SecretSantaDetailAction {

  sealed interface DrawPending : SecretSantaDetailAction
  sealed interface DrawDone : SecretSantaDetailAction

  data object EditEvent : DrawPending
  data object DoDraw : DrawPending

  data object SeeReceiverHobbies : DrawDone
  data class SeeReceiverWishlist(val wishlistId: String) : DrawDone
  data object OpenReceiverChat : DrawDone
  data object ShareGiverWishlist : DrawDone
  data class SeeGiverWishlist(val wishlistId: String) : DrawDone
  data object OpenGiverChat : DrawDone
}