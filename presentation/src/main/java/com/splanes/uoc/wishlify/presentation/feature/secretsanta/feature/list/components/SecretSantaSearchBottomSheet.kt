package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.domain.feature.secresanta.model.SecretSantaEvent
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.SearchBottomSheet
import com.splanes.uoc.wishlify.presentation.common.components.image.ImageOrPlaceholder
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecretSantaSearchBottomSheet(
  visible: Boolean,
  sheetState: SheetState,
  events: List<SecretSantaEvent>,
  modifier: Modifier = Modifier,
  onDismiss: () -> Unit,
  onClick: (SecretSantaEvent) -> Unit,
) {
  SearchBottomSheet(
    modifier = modifier,
    visible = visible,
    sheetState = sheetState,
    items = events,
    title = stringResource(R.string.secret_santa_search),
    description = stringResource(R.string.secret_santa_search_description),
    queryLabel = stringResource(R.string.secret_santa_new_event_name_input_label),
    onDismiss = onDismiss,
    onSearch = { items, query ->
      items.filter { it.name.contains(query, ignoreCase = true) }
    },
    onResultClick = onClick,
    resultContent = { result -> ResultRow(event = result) }
  )
}

@Composable
private fun ResultRow(event: SecretSantaEvent) {
  ImageOrPlaceholder(
    modifier = Modifier
      .size(50.dp)
      .border(
        width = 1.dp,
        color = WishlifyTheme.colorScheme.outline.copy(alpha = .16f),
        shape = WishlifyTheme.shapes.small
      ),
    shape = WishlifyTheme.shapes.small,
    url = event.photoUrl,
    placeholder = painterResource(R.drawable.img_secret_santa_event_placeholder),
  )

  Spacer(Modifier.width(12.dp))

  Text(
    text = event.name,
    style = WishlifyTheme.typography.bodyLarge,
    color = WishlifyTheme.colorScheme.onSurface
  )
}