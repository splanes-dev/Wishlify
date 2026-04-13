package com.splanes.uoc.wishlify.presentation.feature.shared.feature.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.EmptyState
import com.splanes.uoc.wishlify.presentation.common.components.Loader
import com.splanes.uoc.wishlify.presentation.common.components.button.IconButtonShape
import com.splanes.uoc.wishlify.presentation.common.components.button.LoadMoreChatButton
import com.splanes.uoc.wishlify.presentation.common.components.input.SendChatMessageInput
import com.splanes.uoc.wishlify.presentation.feature.shared.feature.chat.components.ChatMessage
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme


@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SharedWishlistThirdPartyChatScreen(
  uiState: SharedWishlistThirdPartyChatUiState.Chat,
  onLoadOlderMessages: () -> Unit,
  onSendMessage: (text: String) -> Unit,
  onBack: () -> Unit,
) {

  val listState = rememberLazyListState()

  val isNearBottom by remember { derivedStateOf { listState.firstVisibleItemIndex <= 1 } }

  var previousLastMessageId by remember { mutableStateOf<String?>(null) }

  LaunchedEffect(uiState.messages) {
    val currentLastMessageId = uiState.messages.firstOrNull()?.id // Reversed as reverseLayout=true

    val appendedNewMessage =
      previousLastMessageId != null &&
          currentLastMessageId != null &&
          previousLastMessageId != currentLastMessageId

    if (appendedNewMessage && isNearBottom) {
      listState.animateScrollToItem(0)
    }

    previousLastMessageId = currentLastMessageId
  }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      TopAppBar(
        title = {
          Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = stringResource(R.string.chat))
            Text(
              text = buildString {
                append(uiState.wishlistName)
                if (uiState.target.isNotBlank()) {
                  append(" - ${uiState.target}")
                }
              },
              style = WishlifyTheme.typography.bodySmall
            )
          }
        },
        navigationIcon = {
          IconButton(
            shapes = IconButtonShape,
            onClick = onBack
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
              contentDescription = stringResource(R.string.back)
            )
          }
        },
      )
    },
  ) { paddings ->

    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddings)
        .padding(
          horizontal = 16.dp,
          vertical = 24.dp
        ),
    ) {
      LazyColumn(
        modifier = Modifier
          .weight(1f)
          .padding(bottom = 16.dp),
        state = listState,
        reverseLayout = true,
        verticalArrangement = Arrangement.spacedBy(16.dp),
      ) {

        item {
          Spacer(Modifier.height(16.dp))
        }

        items(
          items = uiState.messages,
          key = { item -> item.id }
        ) { message ->
          ChatMessage(
            modifier = Modifier
              .fillMaxWidth()
              .animateItem(),
            message = message
          )
        }

        if (uiState.canLoadOlderMessages) {
          item(key = "load-btn") {
            LoadMoreChatButton(
              modifier = Modifier
                .fillMaxWidth()
                .animateItem(),
              isLoading = uiState.isLoading,
              onClick = onLoadOlderMessages
            )
          }
        }
      }

      SendChatMessageInput(
        modifier = Modifier.fillMaxWidth(),
        onSend = onSendMessage
      )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SharedWishlistThirdPartyChatEmptyScreen(
  uiState: SharedWishlistThirdPartyChatUiState.Empty,
  onSendMessage: (text: String) -> Unit,
  onBack: () -> Unit,
) {
  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      TopAppBar(
        title = {
          Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = stringResource(R.string.chat))
            Text(
              text = buildString {
                append(uiState.wishlistName)
                if (uiState.target.isNotBlank()) {
                  append(" - ${uiState.target}")
                }
              },
              style = WishlifyTheme.typography.bodySmall
            )
          }
        },
        navigationIcon = {
          IconButton(
            shapes = IconButtonShape,
            onClick = onBack
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
              contentDescription = stringResource(R.string.back)
            )
          }
        },
      )
    },
  ) { paddings ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(paddings)
        .padding(
          horizontal = 16.dp,
          vertical = 24.dp
        ),
    ) {

      Spacer(Modifier.weight(.5f))

      // Used as error component as well
      EmptyState(
        modifier = Modifier.fillMaxWidth(),
        image = painterResource(R.drawable.img_empty_chat),
        title = stringResource(R.string.shared_wishlists_chat_empty_state_title),
        description = stringResource(R.string.shared_wishlists_chat_empty_state_description)
      )

      Spacer(Modifier.weight(1f))

      SendChatMessageInput(
        modifier = Modifier.fillMaxWidth(),
        onSend = onSendMessage
      )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SharedWishlistThirdPartyChatLoadingScreen(
  uiState: SharedWishlistThirdPartyChatUiState.Loading,
  onBack: () -> Unit,
) {
  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      TopAppBar(
        title = {
          Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = stringResource(R.string.chat))
            Text(
              text = buildString {
                append(uiState.wishlistName)
                if (uiState.target.isNotBlank()) {
                  append(" - ${uiState.target}")
                }
              },
              style = WishlifyTheme.typography.bodySmall
            )
          }
        },
        navigationIcon = {
          IconButton(
            shapes = IconButtonShape,
            onClick = onBack
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
              contentDescription = stringResource(R.string.back)
            )
          }
        },
      )
    },
  ) { paddings ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(paddings)
        .padding(
          horizontal = 16.dp,
          vertical = 24.dp
        ),
      verticalArrangement = Arrangement.Center
    ) {

      Loader(
        modifier = Modifier.fillMaxWidth(),
        containerColor = Color.Transparent
      )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SharedWishlistThirdPartyChatErrorScreen(
  uiState: SharedWishlistThirdPartyChatUiState.Error,
  onBack: () -> Unit,
) {
  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      TopAppBar(
        title = {
          Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = stringResource(R.string.chat))
            Text(
              text = buildString {
                append(uiState.wishlistName)
                if (uiState.target.isNotBlank()) {
                  append(" - ${uiState.target}")
                }
              },
              style = WishlifyTheme.typography.bodySmall
            )
          }
        },
        navigationIcon = {
          IconButton(
            shapes = IconButtonShape,
            onClick = onBack
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
              contentDescription = stringResource(R.string.back)
            )
          }
        },
      )
    },
  ) { paddings ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(paddings)
        .padding(
          horizontal = 16.dp,
          vertical = 24.dp
        ),
    ) {

      Spacer(Modifier.weight(.5f))

      // Used as error component as well
      EmptyState(
        modifier = Modifier.fillMaxWidth(),
        image = painterResource(R.drawable.generic_error),
        title = stringResource(R.string.wishlists_detail_error_title),
        description = stringResource(R.string.wishlists_detail_error_description)
      )

      Spacer(Modifier.weight(1f))
    }
  }
}