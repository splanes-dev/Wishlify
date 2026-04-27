package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.splanes.uoc.wishlify.domain.feature.secretsanta.model.SecretSantaEventDetail
import com.splanes.uoc.wishlify.domain.feature.secretsanta.usecase.DoSecretSantaDrawUseCase
import com.splanes.uoc.wishlify.domain.feature.secretsanta.usecase.FetchSecretSantaDetailUseCase
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiMapper
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.chat.model.SecretSantaChatType
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.detail.model.SecretSantaDetailAction
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

/**
 * Coordinates the Secret Santa detail screen and the actions available from each event state.
 */
class SecretSantaDetailViewModel(
  private val eventId: String,
  eventName: String,
  private val fetchSecretSantaDetailUseCase: FetchSecretSantaDetailUseCase,
  private val doSecretSantaDrawUseCase: DoSecretSantaDrawUseCase,
  private val errorUiMapper: ErrorUiMapper
) : ViewModel() {

  private val viewModelState = MutableStateFlow(ViewModelState(eventName))

  val uiState = viewModelState.asStateFlow()
    .onStart { fetchSecretSantaEvent(eventId) }
    .map { state -> state.toUiState(errorUiMapper) }
    .stateIn(
      initialValue = viewModelState.value.toUiState(errorUiMapper),
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000)
    )

  private val uiSideEffectChannel = Channel<SecretSantaDetailUiSideEffect>()
  val uiSideEffect = uiSideEffectChannel.receiveAsFlow()

  /**
   * Returns the loaded event once the detail fetch has completed.
   */
  suspend fun fetchSecretSantaEvent(): SecretSantaEventDetail.DrawDone {
    val currentState = viewModelState.value
    if (currentState.event != null) {
      return currentState.event as SecretSantaEventDetail.DrawDone
    } else {
      delay(300.milliseconds)
      return fetchSecretSantaEvent()
    }
  }

  /**
   * Refreshes the event after returning from a nested flow that may have modified it.
   */
  fun onEventUpdated() {
    viewModelScope.launch { fetchSecretSantaEvent(eventId) }
  }

  /**
   * Dispatches a detail action to the matching navigation or mutation flow.
   */
  fun onAction(event: SecretSantaEventDetail, action: SecretSantaDetailAction) {
    when (action) {
      SecretSantaDetailAction.OpenGiverChat ->
        onOpenChat(event, SecretSantaChatType.AsReceiver)

      SecretSantaDetailAction.OpenReceiverChat ->
        onOpenChat(event, SecretSantaChatType.AsGiver)

      is SecretSantaDetailAction.SeeGiverWishlist ->
        onOpenSharedWishlist(event = event, isOwnWishlist = true)

      SecretSantaDetailAction.SeeReceiverHobbies ->
        onOpenReceiverHobbies(event)

      is SecretSantaDetailAction.SeeReceiverWishlist ->
        onOpenSharedWishlist(event = event, isOwnWishlist = false)

      SecretSantaDetailAction.ShareGiverWishlist ->
        onShareWishlistToGiver(event)

      SecretSantaDetailAction.DoDraw ->
        onDoDraw(event)

      SecretSantaDetailAction.EditEvent ->
        onEditEvent(event)
    }
  }

  /**
   * Clears the current UI error.
   */
  fun onDismissError() {
    viewModelState.update { state -> state.copy(error = null) }
  }

  /**
   * Loads the Secret Santa event detail for the current event id.
   */
  private suspend fun fetchSecretSantaEvent(id: String) {
    viewModelState.update { state -> state.copy(isLoadingFullscreen = true) }
    val result = fetchSecretSantaDetailUseCase(id)
    viewModelState.update { state ->
      state.copy(
        event = result.getOrNull(),
        isLoadingFullscreen = false
      )
    }
  }

  /**
   * Runs the draw for the current event and reloads the updated detail on success.
   */
  private fun onDoDraw(event: SecretSantaEventDetail) {
    viewModelState.update { state -> state.copy(isLoading = true) }
    viewModelScope.launch {
      doSecretSantaDrawUseCase(event)
        .onSuccess {
          viewModelState.update { state -> state.copy(isLoading = false) }
          fetchSecretSantaEvent(event.id)
        }
        .onFailure { error ->
          viewModelState.update { state ->
            state.copy(
              isLoading = false,
              error = error
            )
          }
        }
    }
  }

  /**
   * Requests navigation to the event edition flow.
   */
  private fun onEditEvent(event: SecretSantaEventDetail) {
    uiSideEffectChannel.trySend(SecretSantaDetailUiSideEffect.NavToEdit(event.id))
  }

  /**
   * Requests navigation to the flow that shares the current user's wishlist with the giver.
   */
  private fun onShareWishlistToGiver(event: SecretSantaEventDetail) {
    uiSideEffectChannel.trySend(SecretSantaDetailUiSideEffect.NavToShareWishlist(event.id))
  }

  /**
   * Requests navigation to either the current user's shared wishlist or the assigned receiver's
   * wishlist.
   */
  private fun onOpenSharedWishlist(
    event: SecretSantaEventDetail,
    isOwnWishlist: Boolean
  ) {
    val e = event as? SecretSantaEventDetail.DrawDone
    val effect = SecretSantaDetailUiSideEffect.NavToWishlist(
      eventId = event.id,
      wishlistOwnerId = e?.receiver?.uid?.takeIf { !isOwnWishlist },
      isOwnWishlist = isOwnWishlist,
    )
    uiSideEffectChannel.trySend(effect)
  }

  /**
   * Requests navigation to the anonymous chat associated with the current participant role.
   */
  private fun onOpenChat(event: SecretSantaEventDetail, type: SecretSantaChatType) {
    val e = event as? SecretSantaEventDetail.DrawDone
      ?: error("Tyring to open chat of secret santa event (${event.id}) but draw is not done...")

    val effect = SecretSantaDetailUiSideEffect.NavToChat(
      eventId = e.id,
      chatType = type.value,
      otherUid = when (type) {
        SecretSantaChatType.AsReceiver -> event.giver
        SecretSantaChatType.AsGiver -> event.receiver.uid
      }
    )

    uiSideEffectChannel.trySend(effect)
  }

  /**
   * Requests navigation to the receiver hobbies screen.
   */
  private fun onOpenReceiverHobbies(event: SecretSantaEventDetail) {
    val e = event as? SecretSantaEventDetail.DrawDone
      ?: error("Tyring to open hobbies of secret santa event (${event.id}) but draw is not done...")

    val effect = SecretSantaDetailUiSideEffect.NavToHobbies(e.receiver.uid)
    uiSideEffectChannel.trySend(effect)
  }

  private data class ViewModelState(
    val eventName: String,
    val event: SecretSantaEventDetail? = null,
    val isLoadingFullscreen: Boolean = true,
    val isLoading: Boolean = false,
    val error: Throwable? = null
  ) {
    /**
     * Maps internal state to the detail UI contract.
     */
    fun toUiState(errorUiMapper: ErrorUiMapper): SecretSantaDetailUiState = when {
      isLoadingFullscreen ->
        SecretSantaDetailUiState.Loading(eventName)

      event == null ->
        SecretSantaDetailUiState.Error(eventName)

      else ->
        SecretSantaDetailUiState.Detail(
          eventName = eventName,
          event = event,
          isLoading = isLoading,
          error = error?.let(errorUiMapper::map),
        )
    }
  }
}
