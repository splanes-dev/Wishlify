package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.splanes.uoc.wishlify.domain.feature.secresanta.model.GetSecretSantaChatRequest
import com.splanes.uoc.wishlify.domain.feature.secresanta.model.SecretSantaChatMessage
import com.splanes.uoc.wishlify.domain.feature.secresanta.model.SecretSantaSendMessageRequest
import com.splanes.uoc.wishlify.domain.feature.secresanta.usecase.FetchSecretSantaChatMessagesUseCase
import com.splanes.uoc.wishlify.domain.feature.secresanta.usecase.SendMessageSecretSantaChatUseCase
import com.splanes.uoc.wishlify.domain.feature.secresanta.usecase.SubscribeSecretSantaChatUseCase
import com.splanes.uoc.wishlify.domain.feature.user.model.User
import com.splanes.uoc.wishlify.domain.feature.user.usecase.FetchUserByIdUseCase
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.chat.model.SecretSantaChatType
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

class SecretSantaChatViewModel(
  private val eventId: String,
  private val chatType: SecretSantaChatType,
  private val otherUid: String,
  private val fetchUserByIdUseCase: FetchUserByIdUseCase,
  private val subscribeSecretSantaChatUseCase: SubscribeSecretSantaChatUseCase,
  private val fetchSecretSantaChatMessagesUseCase: FetchSecretSantaChatMessagesUseCase,
  private val sendMessageSecretSantaChatUseCase: SendMessageSecretSantaChatUseCase,
) : ViewModel() {

  private var observerJob: Job? = null

  private val viewModelState = MutableStateFlow(ViewModelState(chatType))
  val uiState = viewModelState
    .onStart { subscribeToChat() }
    .map { state -> state.toUiState() }
    .stateIn(
      initialValue = viewModelState.value.toUiState(),
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000)
    )

  fun onSendMessage(text: String) {
    viewModelScope.launch {

      val request = when (chatType) {
        SecretSantaChatType.AsReceiver -> SecretSantaSendMessageRequest.AsReceiver(
          eventId = eventId,
          otherUid = otherUid,
          text = text
        )
        SecretSantaChatType.AsGiver -> SecretSantaSendMessageRequest.AsGiver(
          eventId = eventId,
          otherUid = otherUid,
          text = text
        )
      }

      sendMessageSecretSantaChatUseCase(request)
    }
  }

  fun onLoadOlderMessages() {
    val currentState = viewModelState.value
    if (currentState.isLoading || currentState.nextCursor == null) {
      return
    }
    viewModelState.update { state -> state.copy(isLoading = true) }
    viewModelScope.launch {

      val request = when (chatType) {
        SecretSantaChatType.AsReceiver -> GetSecretSantaChatRequest.AsReceiver(eventId, otherUid)
        SecretSantaChatType.AsGiver -> GetSecretSantaChatRequest.AsGiver(eventId, otherUid)
      }

      fetchSecretSantaChatMessagesUseCase(
        request = request,
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

  private suspend fun subscribeToChat() {
    viewModelState.update { state -> state.copy(isLoadingFullscreen = true) }

    val receiver = if (chatType == SecretSantaChatType.AsGiver) {
      fetchUserByIdUseCase(otherUid).getOrNull()
    } else {
      null
    }

    val request = when (chatType) {
      SecretSantaChatType.AsReceiver -> GetSecretSantaChatRequest.AsReceiver(eventId, otherUid)
      SecretSantaChatType.AsGiver -> GetSecretSantaChatRequest.AsGiver(eventId, otherUid)
    }

    subscribeSecretSantaChatUseCase(request)
      .onSuccess { flow ->
        observerJob?.cancel()
        observerJob = viewModelScope.launch {
          flow
            .catch { error ->
              viewModelState.update { state ->
                state.copy(
                  isLoadingFullscreen = false,
                  receiver = receiver,
                  error = error
                )
              }
            }
            .collect { messages ->
              viewModelState.update { state ->
                val all = state.messages + messages
                state.copy(
                  isLoadingFullscreen = false,
                  receiver = receiver,
                  messages = all,
                  canLoadOlderMessages = state.canLoadOlderMessages && all.count() == 30,
                  nextCursor = state.nextCursor ?: all.firstOrNull()?.sentAt?.time
                )
              }
            }
        }
      }
      .onFailure { error ->
        viewModelState.update { state ->
          state.copy(
            isLoadingFullscreen = false,
            receiver = receiver,
            error = error,
          )
        }
      }
  }

  private data class ViewModelState(
    val type: SecretSantaChatType,
    val receiver: User.Basic? = null,
    val messages: List<SecretSantaChatMessage> = emptyList(),
    val canLoadOlderMessages: Boolean = true,
    val nextCursor: Long? = null,
    val page: Int = 1,
    val isLoadingFullscreen: Boolean = true,
    val isLoading: Boolean = false,
    val error: Throwable? = null
  ) {
    fun toUiState() = when {
      isLoadingFullscreen ->
        SecretSantaChatUiState.Loading(type = type, receiver = receiver)

      error != null ->
        SecretSantaChatUiState.Error(type = type, receiver = receiver)

      messages.isEmpty() ->
        SecretSantaChatUiState.Empty(type = type, receiver = receiver)

      type == SecretSantaChatType.AsGiver ->
        receiver?.let {
          SecretSantaChatUiState.ChatAsGiver(
            receiver = receiver,
            messages = messages
              .distinctBy { it.messageId }
              .sortedByDescending { it.sentAt },
            isLoading = isLoading,
            canLoadOlderMessages = canLoadOlderMessages
          )
        } ?: SecretSantaChatUiState.Error(type = type, receiver = null)

      else ->
        SecretSantaChatUiState.ChatAsReceiver(
          messages = messages
            .distinctBy { it.messageId }
            .sortedByDescending { it.sentAt },
          isLoading = isLoading,
          canLoadOlderMessages = canLoadOlderMessages
        )
    }
  }
}