package com.splanes.uoc.wishlify.presentation.feature.profile.feature.notifications.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@Composable
fun NotificationSwitch(
  checked: Boolean,
  text: String,
  isBottomDividerVisible: Boolean,
  modifier: Modifier = Modifier,
  onCheckedChange: (Boolean) -> Unit,
) {
  Column(modifier = modifier) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Text(
        modifier = Modifier.weight(1f),
        text = text,
        style = WishlifyTheme.typography.labelLarge,
        color = WishlifyTheme.colorScheme.onSurface,
      )

      Switch(
        checked = checked,
        onCheckedChange = onCheckedChange
      )
    }

    if (isBottomDividerVisible) {
      HorizontalDivider(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp),
        color = WishlifyTheme.colorScheme.outlineVariant
      )
    } else {
      Spacer(Modifier.height(8.dp))
    }
  }
}