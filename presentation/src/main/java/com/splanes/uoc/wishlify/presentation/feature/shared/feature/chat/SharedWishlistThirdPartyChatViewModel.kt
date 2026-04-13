package com.splanes.uoc.wishlify.presentation.feature.shared.feature.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlistChatMessage
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlistSendMessageRequest
import com.splanes.uoc.wishlify.domain.feature.shared.usecase.FetchSharedWishlistChatOlderMessagesUseCase
import com.splanes.uoc.wishlify.domain.feature.shared.usecase.SendMessageSharedWishlistChatUseCase
import com.splanes.uoc.wishlify.domain.feature.shared.usecase.SubscribeSharedWishlistChatUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

class SharedWishlistThirdPartyChatViewModel(
  private val sharedWishlistId: String,
  sharedWishlistName: String,
  target: String,
  private val sendMessageSharedWishlistChatUseCase: SendMessageSharedWishlistChatUseCase,
  private val subscribeSharedWishlistChatUseCase: SubscribeSharedWishlistChatUseCase,
  private val fetchSharedWishlistChatOlderMessagesUseCase: FetchSharedWishlistChatOlderMessagesUseCase,
) : ViewModel() {

  private var observerJob: Job? = null

  private val viewModelState = MutableStateFlow(ViewModelState(sharedWishlistName, target))
  val uiState = viewModelState
    .map { state -> state.toUiState() }
    .stateIn(
      initialValue = viewModelState.value.toUiState(),
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000)
    )

  init {
    subscribeToChat()
  }

  fun onSendMessage(text: String) {
    viewModelScope.launch {
      val request = SharedWishlistSendMessageRequest(wishlist = sharedWishlistId, text = text)
      sendMessageSharedWishlistChatUseCase(request)
    }
  }

  fun onLoadOlderMessages() {
    val currentState = viewModelState.value
    if (currentState.isLoading || currentState.nextCursor == null) {
      return
    }
    viewModelState.update { state -> state.copy(isLoading = true) }
    viewModelScope.launch {
      fetchSharedWishlistChatOlderMessagesUseCase(
        wishlistId = sharedWishlistId,
        cursor = currentState.nextCursor
      ).onSuccess { page ->
        viewModelState.update { state ->
          state.copy(
            isLoading = false,
            nextCursor = page.nextCursor,
            canLoadOlderMessages = page.hasMore,
            messages = page.messages + currentState.messages
          )
        }
      }.onFailure { error ->
        Timber.e(error)
        viewModelState.update { state -> state.copy(isLoading = false) }
      }
    }
  }

  private fun subscribeToChat() {
    viewModelState.update { state -> state.copy(isLoadingFullscreen = true) }
    subscribeSharedWishlistChatUseCase(sharedWishlistId)
      .onSuccess { flow ->
        observerJob?.cancel()
        observerJob = viewModelScope.launch {
          flow
            .catch { error ->
              viewModelState.update { state ->
                state.copy(
                  isLoadingFullscreen = false,
                  error = error
                )
              }
            }
            .collect { messages ->
              viewModelState.update { state ->
                val all = state.messages + messages
                state.copy(
                  isLoadingFullscreen = false,
                  messages = all,
                  canLoadOlderMessages = state.canLoadOlderMessages && all.count() == 30,
                  nextCursor = state.nextCursor ?: all.firstOrNull()?.createdAt?.time
                )
              }
            }
        }
      }
      .onFailure { error ->
        viewModelState.update { state ->
          state.copy(
            isLoadingFullscreen = false,
            error = error,
          )
        }
      }
  }

  private data class ViewModelState(
    val sharedWishlistName: String,
    val sharedWishlistTarget: String,
    val isLoadingFullscreen: Boolean = true,
    val isLoading: Boolean = false,
    val messages: List<SharedWishlistChatMessage> = emptyList(),
    val canLoadOlderMessages: Boolean = true,
    val nextCursor: Long? = null,
    val page: Int = 1,
    val error: Throwable? = null
  ) {

    fun toUiState(): SharedWishlistThirdPartyChatUiState =
      when {
        isLoadingFullscreen ->
          SharedWishlistThirdPartyChatUiState.Loading(
            wishlistName = sharedWishlistName,
            target = sharedWishlistTarget
          )

        error != null ->
          SharedWishlistThirdPartyChatUiState.Error(
            wishlistName = sharedWishlistName,
            target = sharedWishlistTarget
          )

        messages.isEmpty() ->
          SharedWishlistThirdPartyChatUiState.Empty(
            wishlistName = sharedWishlistName,
            target = sharedWishlistTarget
          )

        else ->
          SharedWishlistThirdPartyChatUiState.Chat(
            wishlistName = sharedWishlistName,
            target = sharedWishlistTarget,
            messages = messages
              .distinctBy { it.id }
              .sortedByDescending { it.createdAt },
            isLoading = isLoading,
            canLoadOlderMessages = canLoadOlderMessages
          )
      }
  }
}