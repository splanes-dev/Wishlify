package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.detail.model

/**
 * Actions available from the Secret Santa event detail screen.
 */
sealed interface SecretSantaDetailAction {

  /**
   * Actions available before the draw has been executed.
   */
  sealed interface DrawPending : SecretSantaDetailAction

  /**
   * Actions available once the draw has been executed.
   */
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
