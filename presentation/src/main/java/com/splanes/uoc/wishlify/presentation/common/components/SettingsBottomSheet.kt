package com.splanes.uoc.wishlify.presentation.common.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsBottomSheet(
  visible: Boolean,
  sheetState: SheetState,
  settings: List<SettingsBottomSheet.Option>,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier,
  title: String? = stringResource(R.string.options),
  onSettingClick: (SettingsBottomSheet.Option) -> Unit = {},
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
        if (title != null) {
          Text(
            modifier = Modifier.fillMaxWidth(),
            text = title,
            style = WishlifyTheme.typography.titleLarge,
            color = WishlifyTheme.colorScheme.onSurface
          )

          Spacer(modifier = Modifier.height(16.dp))
        }

        settings.forEachIndexed { index, option ->
          SettingOption(
            icon = option.icon,
            text = option.text,
            contentColor = option.contentColor ?: WishlifyTheme.colorScheme.onSurfaceVariant,
            onClick = { onSettingClick(option) }
          )

          if (index != settings.lastIndex) {

            Spacer(modifier = Modifier.height(4.dp))

            HorizontalDivider(
              modifier = Modifier.fillMaxWidth(),
              color = WishlifyTheme.colorScheme.outline
            )

            Spacer(modifier = Modifier.height(4.dp))
          }
        }

        Spacer(modifier = Modifier.height(16.dp))
      }
    }
  }
}

@Composable
private fun SettingOption(
  icon: ImageVector,
  text: String,
  contentColor: Color,
  onClick: () -> Unit,
) {
  Surface(
    modifier = Modifier.fillMaxWidth(),
    shape = WishlifyTheme.shapes.small,
    color = Color.Transparent,
    onClick = onClick,
  ) {
    Row(
      modifier = Modifier.padding(8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Icon(
        imageVector = icon,
        contentDescription = text,
        tint = contentColor
      )

      Text(
        text = text,
        style = WishlifyTheme.typography.titleMedium,
        color = contentColor,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }
  }

}

object SettingsBottomSheet {
  data class Option(
    val id: String,
    val icon: ImageVector,
    val text: String,
    val contentColor: Color? = null,
  )
}