package com.splanes.uoc.wishlify.presentation.feature.groups.feature.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.PersonSearch
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.domain.feature.user.model.User
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.ErrorDialog
import com.splanes.uoc.wishlify.presentation.common.components.Loader
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonShape
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonText
import com.splanes.uoc.wishlify.presentation.common.components.button.IconButtonShape
import com.splanes.uoc.wishlify.presentation.common.components.input.text.TextInput
import com.splanes.uoc.wishlify.presentation.common.components.input.text.rememberTextInputState
import com.splanes.uoc.wishlify.presentation.feature.groups.feature.search.components.SearchUsersInfoBanner
import com.splanes.uoc.wishlify.presentation.feature.groups.feature.search.components.SearchUsersQueryResults
import com.splanes.uoc.wishlify.presentation.feature.groups.feature.search.components.SearchUsersResultsAdded
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun GroupsSearchUsersScreen(
  uiState: GroupsSearchUsersUiState,
  onSearch: (query: String) -> Unit,
  onAddUser: (user: User.Basic) -> Unit,
  onRemoveUser: (user: User.Basic) -> Unit,
  onSave: (List<User.Basic>) -> Unit,
  onCloseInfoBanner: () -> Unit,
  onDismissError: () -> Unit,
  onBack: () -> Unit,
) {

  val queryState = rememberTextInputState()

  val isButtonEnabled by remember(uiState.added) {
    derivedStateOf { uiState.added.isNotEmpty() }
  }

  LaunchedEffect(uiState.searchQuery) {
    queryState.onValueChanged(uiState.searchQuery)
  }

  Box(modifier = Modifier.fillMaxSize()) {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        TopAppBar(
          title = { Text(text = stringResource(R.string.groups_search_users)) },
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
      }
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
        Column(
          modifier = Modifier
            .weight(1f)
            .verticalScroll(rememberScrollState())
        ) {
          Text(
            text = stringResource(R.string.groups_search_users_description),
            style = WishlifyTheme.typography.bodyLarge,
            color = WishlifyTheme.colorScheme.onSurface,
          )

          AnimatedVisibility(
            modifier = Modifier.padding(top = 16.dp),
            visible = uiState.isInfoBannerVisible,
            enter = EnterTransition.None,
          ) {
            SearchUsersInfoBanner(onClose = onCloseInfoBanner)
          }

          TextInput(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 16.dp),
            state = queryState,
            label = stringResource(R.string.searcher),
            leadingIcon = Icons.Outlined.PersonSearch,
            trailingIcon = {
              IconButton(
                onClick = { onSearch(queryState.text) }
              ) {
                Icon(
                  imageVector = Icons.Rounded.Search,
                  contentDescription = stringResource(R.string.searcher),
                  tint = WishlifyTheme.colorScheme.onSurface.copy(alpha = .7f)
                )
              }
            },
            singleLine = true,
            cleanable = false,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearch(queryState.text) })
          )

          AnimatedVisibility(
            modifier = Modifier.padding(top = 16.dp),
            visible = uiState.searchQuery.isNotEmpty(),
            enter = expandVertically(),
            exit = shrinkVertically()
          ) {
            SearchUsersQueryResults(
              modifier = Modifier.fillMaxWidth(),
              results = uiState.results,
              added = uiState.added,
              onAddUser = onAddUser
            )
          }

          AnimatedVisibility(
            modifier = Modifier.padding(top = 16.dp),
            visible = uiState.added.isNotEmpty(),
            enter = expandVertically(),
            exit = shrinkVertically()
          ) {
            SearchUsersResultsAdded(
              modifier = Modifier.fillMaxWidth(),
              added = uiState.added,
              onRemoveUser = onRemoveUser
            )
          }
        }

        Button(
          modifier = Modifier.fillMaxWidth(),
          shapes = ButtonShape,
          enabled = isButtonEnabled,
          onClick = { onSave(uiState.added) }
        ) {
          ButtonText(text = stringResource(R.string.save))
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