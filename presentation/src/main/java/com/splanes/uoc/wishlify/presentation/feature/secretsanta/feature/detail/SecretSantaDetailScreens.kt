package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.domain.feature.secretsanta.model.SecretSantaEventDetail
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.EmptyState
import com.splanes.uoc.wishlify.presentation.common.components.ErrorDialog
import com.splanes.uoc.wishlify.presentation.common.components.Loader
import com.splanes.uoc.wishlify.presentation.common.components.button.IconButtonShape
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.detail.components.SecretSantaDetailHeader
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.detail.components.SecretSantaDrawDoneDetailContent
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.detail.components.SecretSantaDrawPendingDetailContent
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.detail.components.SecretSantaInfoBottomSheet
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.detail.model.SecretSantaDetailAction

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SecretSantaDetailScreen(
  uiState: SecretSantaDetailUiState.Detail,
  onAction: (event: SecretSantaEventDetail, action: SecretSantaDetailAction) -> Unit,
  onDismissError: () -> Unit,
  onBack: () -> Unit,
) {

  var isEventInfoModalOpen by remember { mutableStateOf(false) }
  val eventInfoSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  Box(modifier = Modifier.fillMaxSize()) {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        TopAppBar(
          title = { Text(text = uiState.eventName) },
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
          actions = {
            IconButton(
              shapes = IconButtonShape,
              onClick = { isEventInfoModalOpen = true }
            ) {
              Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = stringResource(R.string.info)
              )
            }
          }
        )
      },
    ) { paddings ->
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(paddings)
          .padding(horizontal = 16.dp)
          .padding(bottom = 24.dp),
      ) {

        SecretSantaDetailHeader(
          modifier = Modifier.fillMaxWidth(),
          detail = uiState.event
        )

        Column(
          modifier = Modifier
            .weight(1f)
            .verticalScroll(rememberScrollState())
        ) {
          when (val event = uiState.event) {
            is SecretSantaEventDetail.DrawDone ->
              SecretSantaDrawDoneDetailContent(
                modifier = Modifier
                  .weight(1f)
                  .padding(top = 16.dp),
                event = event,
                onAction = { action -> onAction(event, action) }
              )

            is SecretSantaEventDetail.DrawPending ->
              SecretSantaDrawPendingDetailContent(
                modifier = Modifier
                  .weight(1f)
                  .padding(top = 16.dp),
                event = event,
                onAction = { action -> onAction(event, action) }
              )
          }
        }
      }
    }

    SecretSantaInfoBottomSheet(
      visible = isEventInfoModalOpen,
      sheetState = eventInfoSheetState,
      event = uiState.event,
      onDismiss = { isEventInfoModalOpen = false }
    )

    uiState.error?.let { error ->
      ErrorDialog(
        uiModel = error,
        onDismiss = onDismissError,
      )
    }

    if (uiState.isLoading) {
      Loader(modifier = Modifier.fillMaxSize())
    }
  }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SecretSantaDetailLoadingScreen(
  uiState: SecretSantaDetailUiState.Loading,
  onBack: () -> Unit,
) {
  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      TopAppBar(
        title = { Text(text = uiState.eventName) },
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
      Loader(
        modifier = Modifier.weight(1f),
        containerColor = Color.Transparent
      )
    }
  }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SecretSantaDetailErrorScreen(
  uiState: SecretSantaDetailUiState.Error,
  onBack: () -> Unit,
) {
  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      TopAppBar(
        title = { Text(text = uiState.eventName) },
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

      // Used as error component as well
      EmptyState(
        modifier = Modifier.fillMaxWidth(),
        image = painterResource(R.drawable.generic_error),
        title = stringResource(R.string.wishlists_detail_error_title),
        description = stringResource(R.string.wishlists_detail_error_description)
      )
    }
  }
}