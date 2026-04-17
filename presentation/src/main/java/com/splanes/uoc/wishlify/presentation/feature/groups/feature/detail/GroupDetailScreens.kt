package com.splanes.uoc.wishlify.presentation.feature.groups.feature.detail

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.domain.feature.groups.model.Group
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.ConfirmationDialog
import com.splanes.uoc.wishlify.presentation.common.components.EmptyState
import com.splanes.uoc.wishlify.presentation.common.components.ErrorDialog
import com.splanes.uoc.wishlify.presentation.common.components.Loader
import com.splanes.uoc.wishlify.presentation.common.components.button.IconButtonShape
import com.splanes.uoc.wishlify.presentation.common.components.image.ImageOrPlaceholder
import com.splanes.uoc.wishlify.presentation.common.utils.copyToClipboard
import com.splanes.uoc.wishlify.presentation.feature.groups.feature.components.GroupSettingsBottomSheet
import com.splanes.uoc.wishlify.presentation.feature.groups.feature.detail.components.GroupDetailHeader
import com.splanes.uoc.wishlify.presentation.feature.groups.feature.detail.components.GroupMember
import com.splanes.uoc.wishlify.presentation.feature.groups.feature.model.GroupSettings
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailScreen(
  uiState: GroupDetailUiState.Detail,
  onEditGroup: (Group) -> Unit,
  onLeaveGroup: (Group) -> Unit,
  onDismissError: () -> Unit,
  onBack: () -> Unit
) {

  val context = LocalContext.current
  val resources = LocalResources.current
  val clipboard = LocalClipboard.current
  val scope = rememberCoroutineScope()

  val settingsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var isSettingsModalOpen by remember { mutableStateOf(false) }

  var isConfirmModalOpen by remember { mutableStateOf(false) }

  Box(modifier = Modifier.fillMaxSize()) {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        TopAppBar(
          title = {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              ImageOrPlaceholder(
                modifier = Modifier
                  .size(40.dp)
                  .border(
                    width = 1.dp,
                    color = WishlifyTheme.colorScheme.outline.copy(alpha = .16f),
                    shape = WishlifyTheme.shapes.small
                  ),
                shape = WishlifyTheme.shapes.small,
                url = uiState.group.photoUrl,
                placeholder = painterResource(R.drawable.preset_group),
              )
              Text(text = uiState.group.name)
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
          actions = {
            if (uiState.group.isInactive) {
              IconButton(
                shapes = IconButtonShape,
                onClick = { isSettingsModalOpen = true }
              ) {
                Icon(
                  imageVector = Icons.Rounded.Tune,
                  contentDescription = stringResource(R.string.settings)
                )
              }
            }
          }
        )
      }
    ) { paddings ->
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(paddings),
        contentPadding = PaddingValues(
          horizontal = 16.dp,
          vertical = 24.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
      ) {
        stickyHeader {
          GroupDetailHeader(
            modifier = Modifier.fillMaxWidth(),
            group = uiState.group,
            onSharedWishlistsClick = {},
            onSecretSantaClick = {},
          )
        }

        items(
          items = uiState.group
            .members
            .sortedByDescending { user -> user.uid != uiState.group.currentUserUid },
          key = { user -> user.uid }
        ) { user ->
          GroupMember(
            modifier = Modifier
              .fillMaxWidth()
              .animateItem(),
            user = user,
            isCurrentUser = uiState.group.currentUserUid == user.uid,
            onClick = {
              scope.launch {
                clipboard.copyToClipboard(
                  label = resources.getString(R.string.groups_member_code),
                  text = user.code
                )
                Toast.makeText(
                  context,
                  R.string.groups_member_code_clipboard_copied,
                  Toast.LENGTH_SHORT
                ).show()
              }
            }
          )
        }
      }
    }

    GroupSettingsBottomSheet(
      visible = isSettingsModalOpen,
      sheetState = settingsSheetState,
      onDismiss = { isSettingsModalOpen = false },
      onSettingClick = { setting ->
        scope
          .launch { settingsSheetState.hide() }
          .invokeOnCompletion { isSettingsModalOpen = false }

        when (setting) {
          GroupSettings.Edit ->
            onEditGroup(uiState.group)

          GroupSettings.LeaveGroup ->
            isConfirmModalOpen = true
        }
      }
    )

    if (isConfirmModalOpen) {
      ConfirmationDialog(
        onDismiss = { isConfirmModalOpen = false },
        onConfirm = { onLeaveGroup(uiState.group) }
      )
    }

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

@Composable
fun GroupDetailLoadingScreen(
  uiState: GroupDetailUiState.Loading,
  onBack: () -> Unit
) {
  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      TopAppBar(
        title = { Text(text = uiState.groupName) },
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

@Composable
fun GroupDetailErrorScreen(
  uiState: GroupDetailUiState.Error,
  onBack: () -> Unit,
) {
  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      TopAppBar(
        title = { Text(text = uiState.groupName) },
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