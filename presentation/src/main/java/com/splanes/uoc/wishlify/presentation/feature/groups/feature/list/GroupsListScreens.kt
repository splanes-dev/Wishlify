package com.splanes.uoc.wishlify.presentation.feature.groups.feature.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.EmptyState
import com.splanes.uoc.wishlify.presentation.common.components.ErrorDialog
import com.splanes.uoc.wishlify.presentation.common.components.Loader
import com.splanes.uoc.wishlify.presentation.feature.groups.feature.list.components.GroupCard
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsListScreen(
  uiState: GroupsListUiState.Groups,
  onNewGroup: () -> Unit,
  onDismissError: () -> Unit,
) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .padding(bottom = 72.dp) // Bottom bar
  ) {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        TopAppBar(
          title = { Text(text = stringResource(R.string.groups)) },
        )
      },
      floatingActionButton = {
        FloatingActionButton(
          shape = WishlifyTheme.shapes.medium,
          containerColor = WishlifyTheme.colorScheme.tertiaryContainer,
          contentColor = WishlifyTheme.colorScheme.onTertiaryContainer,
          onClick = onNewGroup,
        ) {
          Icon(
            imageVector = Icons.Rounded.Add,
            contentDescription = null,
          )
        }
      },
      floatingActionButtonPosition = FabPosition.End
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
        items(uiState.groups) { group ->
          GroupCard(
            modifier = Modifier.fillMaxWidth(),
            group = group,
            onSettingsClick = {},
            onClick = {}
          )
        }
      }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsListEmptyScreen(
  uiState: GroupsListUiState.Empty,
  onNewGroup: () -> Unit,
  onDismissError: () -> Unit,
) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .padding(bottom = 72.dp) // Bottom bar
  ) {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        TopAppBar(
          title = { Text(text = stringResource(R.string.groups)) },
        )
      },
      floatingActionButton = {
        FloatingActionButton(
          modifier = Modifier.padding(16.dp),
          shape = WishlifyTheme.shapes.medium,
          containerColor = WishlifyTheme.colorScheme.tertiaryContainer,
          contentColor = WishlifyTheme.colorScheme.onTertiaryContainer,
          onClick = onNewGroup,
        ) {
          Icon(
            imageVector = Icons.Rounded.Add,
            contentDescription = null,
          )
        }
      },
      floatingActionButtonPosition = FabPosition.End
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
        Spacer(modifier = Modifier.height(80.dp))

        EmptyState(
          modifier = Modifier.fillMaxWidth(),
          image = painterResource(R.drawable.groups_empty_state),
          title = stringResource(R.string.wishlists_list_empty_state_title),
          description = stringResource(R.string.groups_list_empty_state_description)
        )
      }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsListLoadingScreen() {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .padding(bottom = 72.dp) // Bottom bar
  ) {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        TopAppBar(
          title = { Text(text = stringResource(R.string.groups)) },
        )
      }
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
          modifier = Modifier.weight(1f),
          containerColor = Color.Transparent
        )
      }
    }
  }
}