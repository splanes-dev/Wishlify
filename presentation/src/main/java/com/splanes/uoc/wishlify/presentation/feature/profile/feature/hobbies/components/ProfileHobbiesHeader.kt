package com.splanes.uoc.wishlify.presentation.feature.profile.feature.hobbies.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@Composable
fun ProfileHobbiesHeader(
  enabled: Boolean,
  modifier: Modifier = Modifier,
  onChange: (Boolean) -> Unit,
) {
  Column(
    modifier = modifier,
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(32.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        modifier = Modifier.weight(1f),
        text = stringResource(R.string.profile_hobbies_description),
        style = WishlifyTheme.typography.labelLarge,
        color = WishlifyTheme.colorScheme.onSurface
      )

      Switch(
        checked = enabled,
        onCheckedChange = onChange
      )
    }

    HorizontalDivider(
      modifier = Modifier.fillMaxWidth(),
      color = WishlifyTheme.colorScheme.outlineVariant
    )
  }
}