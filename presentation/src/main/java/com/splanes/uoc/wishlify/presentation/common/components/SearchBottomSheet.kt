package com.splanes.uoc.wishlify.presentation.common.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.input.text.TextInput
import com.splanes.uoc.wishlify.presentation.common.components.input.text.rememberTextInputState
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
fun <T> SearchBottomSheet(
  visible: Boolean,
  sheetState: SheetState,
  items: List<T>,
  title: String,
  description: String,
  queryLabel: String,
  modifier: Modifier = Modifier,
  onDismiss: () -> Unit,
  onSearch: (items: List<T>, query: String) -> List<T>,
  onResultClick: (T) -> Unit,
  resultContent: @Composable RowScope.(T) -> Unit,
) {
  if (visible) {

    val queryState = rememberTextInputState()
    var query by remember { mutableStateOf("") }

    LaunchedEffect(queryState) {
      snapshotFlow { queryState.text }
        .debounce(500.milliseconds)
        .distinctUntilChanged()
        .collect { text -> query = text }
    }

    val results by remember {
      derivedStateOf { onSearch(items, query) }
    }

    ModalBottomSheet(
      modifier = modifier,
      sheetState = sheetState,
      onDismissRequest = onDismiss
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(
            imageVector = Icons.Rounded.Search,
            contentDescription = title,
            tint = WishlifyTheme.colorScheme.onSurface
          )

          Text(
            text = title,
            style = WishlifyTheme.typography.titleLarge,
            color = WishlifyTheme.colorScheme.onSurface
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = description,
          style = WishlifyTheme.typography.bodyMedium,
          color = WishlifyTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextInput(
          modifier = Modifier.fillMaxWidth(),
          state = queryState,
          label = queryLabel,
          leadingIcon = Icons.Rounded.Search,
          cleanable = false,
          singleLine = true
        )

        AnimatedVisibility(
          modifier = Modifier.padding(top = 8.dp),
          visible = query.isNotBlank(),
          enter = expandVertically(),
          exit = shrinkVertically()
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .verticalScroll(rememberScrollState())
          ) {

            HorizontalDivider(
              modifier = Modifier.fillMaxWidth(),
              color = WishlifyTheme.colorScheme.outline
            )

            Spacer(Modifier.height(8.dp))

            Text(
              text = stringResource(R.string.search_results),
              style = WishlifyTheme.typography.titleSmall,
              color = WishlifyTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(16.dp))

            if (results.isNotEmpty()) {
              results.forEach { result ->
                ResultRow(
                  onClick = { onResultClick(result) },
                  content = { resultContent(result) }
                )
              }
            } else {
              Text(
                text = stringResource(R.string.search_results_no_results_prompt),
                style = WishlifyTheme.typography.bodySmall,
                color = WishlifyTheme.colorScheme.outline
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }
}

@Composable
private fun ResultRow(
  onClick: () -> Unit,
  content: @Composable RowScope.() -> Unit,
) {
  Surface(
    shape = WishlifyTheme.shapes.small,
    color = Color.Transparent,
    onClick = onClick
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(
          vertical = 4.dp,
          horizontal = 8.dp
        ),
      verticalAlignment = Alignment.CenterVertically
    ) {
      content()
    }
  }
}