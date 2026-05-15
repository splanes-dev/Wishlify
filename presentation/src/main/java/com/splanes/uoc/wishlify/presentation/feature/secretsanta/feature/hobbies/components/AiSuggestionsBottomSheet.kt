package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.hobbies.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.hobbies.model.GiftSuggestionUiModel
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiSuggestionsBottomSheet(
  visible: Boolean,
  sheetState: SheetState,
  suggestions: List<GiftSuggestionUiModel>,
  modifier: Modifier = Modifier,
  onDismiss: () -> Unit,
) {
  if (visible) {
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
        Text(
          modifier = Modifier.fillMaxWidth(),
          text = stringResource(R.string.gift_suggestion_bottom_sheet_title),
          style = WishlifyTheme.typography.titleLarge,
          color = WishlifyTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        InfoBanner()

        Spacer(modifier = Modifier.height(16.dp))

        Column(
          modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
        ) {
          if (suggestions.isNotEmpty()) {
            suggestions.forEachIndexed { index, suggestion ->
              SuggestionRow(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(bottom = if (index != suggestions.lastIndex) 8.dp else 0.dp),
                index = index + 1,
                suggestion = suggestion
              )
            }
          } else {
            EmptyResults()
          }
        }

        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }
}

@Composable
private fun InfoBanner(modifier: Modifier = Modifier) {
  Surface(
    modifier = modifier,
    shape = WishlifyTheme.shapes.small,
    color = WishlifyTheme.colorScheme.infoContainer
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 4.dp, start = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Icon(
          imageVector = Icons.Outlined.Info,
          contentDescription = stringResource(R.string.info),
          tint = WishlifyTheme.colorScheme.onInfoContainer
        )

        Spacer(Modifier.width(8.dp))

        Text(
          text = stringResource(R.string.important),
          style = WishlifyTheme.typography.bodyMedium,
          color = WishlifyTheme.colorScheme.onInfoContainer,
          fontWeight = FontWeight.Bold
        )
      }

      Text(
        modifier = Modifier
          .fillMaxWidth()
          .padding(
            start = 40.dp,
            end = 32.dp
          ),
        text = stringResource(R.string.gift_suggestion_bottom_sheet_info_banner),
        style = WishlifyTheme.typography.bodyMedium,
        color = WishlifyTheme.colorScheme.onInfoContainer,
        textAlign = TextAlign.Justify
      )

      Spacer(Modifier.height(8.dp))
    }
  }
}

@Composable
private fun SuggestionRow(
  index: Int,
  suggestion: GiftSuggestionUiModel,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier,
    verticalArrangement = Arrangement.spacedBy(2.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

      Text(
        modifier = Modifier.width(16.dp),
        text = "$index.",
        style = WishlifyTheme.typography.bodyLarge,
        color = WishlifyTheme.colorScheme.onSurface,
        fontWeight = FontWeight.Bold
      )

      Text(
        text = suggestion.text,
        style = WishlifyTheme.typography.bodyLarge,
        color = WishlifyTheme.colorScheme.onSurface,
        fontWeight = FontWeight.Bold
      )
    }

    Text(
      modifier = Modifier.padding(start = 24.dp),
      text = when {
        suggestion.confidence >= .75f -> R.string.gift_suggestion_confidence_high
        suggestion.confidence >= .35f -> R.string.gift_suggestion_confidence_med
        else -> R.string.gift_suggestion_confidence_low
      }.let { id -> stringResource(id) },
      style = WishlifyTheme.typography.bodySmall,
      color = suggestion.color(),
    )
  }
}

@Composable
private fun EmptyResults() {
  Surface(
    shape = WishlifyTheme.shapes.small,
    color = WishlifyTheme.colorScheme.surfaceVariant
  ) {
    Text(
      modifier = Modifier.padding(
        horizontal = 8.dp,
        vertical = 4.dp
      ),
      text = stringResource(R.string.gift_suggestion_bottom_sheet_empty),
      style = WishlifyTheme.typography.bodyMedium,
      color = WishlifyTheme.colorScheme.onSurfaceVariant
    )
  }
}

@Composable
private fun GiftSuggestionUiModel.color() = when {
  confidence > .75f -> WishlifyTheme.colorScheme.success
  confidence > .35f -> WishlifyTheme.colorScheme.warning
  else -> WishlifyTheme.colorScheme.onSurface
}