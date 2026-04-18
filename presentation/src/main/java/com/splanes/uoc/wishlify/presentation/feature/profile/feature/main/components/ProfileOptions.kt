package com.splanes.uoc.wishlify.presentation.feature.profile.feature.main.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.main.model.ProfileOption
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@Composable
fun ProfileOptions(
  options: List<ProfileOption>,
  modifier: Modifier = Modifier,
  onOptionClick: (ProfileOption) -> Unit,
) {
  Column(
    modifier = modifier,
  ) {
    options.forEachIndexed { index, option ->

      if (index == 0) {
        HorizontalDivider(
          modifier = Modifier.fillMaxWidth(),
          color = WishlifyTheme.colorScheme.outline.copy(alpha = .3f)
        )
      }

      ProfileOptionItem(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 2.dp),
        text = option.text(),
        onClick = { onOptionClick(option) }
      )

      HorizontalDivider(
        modifier = Modifier.fillMaxWidth(),
        color = WishlifyTheme.colorScheme.outline.copy(alpha = .3f)
      )
    }
  }
}

@Composable
private fun ProfileOptionItem(
  text: String,
  modifier: Modifier = Modifier,
  onClick: () -> Unit,
) {
  Surface(
    modifier = modifier,
    shape = WishlifyTheme.shapes.small,
    color = Color.Transparent,
    onClick = onClick
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(
        text = text,
        style = WishlifyTheme.typography.bodyLarge,
        color = WishlifyTheme.colorScheme.onSurfaceVariant,
      )

      Icon(
        imageVector = Icons.Rounded.ChevronRight,
        contentDescription = null,
        tint = WishlifyTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
}

@Composable
private fun ProfileOption.text() = when (this) {
  ProfileOption.UpdateProfile -> R.string.profile_update_profile
  ProfileOption.ChangePassword -> R.string.profile_change_password
  ProfileOption.AdminNotifications -> R.string.profile_admin_notifications
  ProfileOption.Store ->  R.string.profile_admin_store
  ProfileOption.Hobbies ->  R.string.profile_admin_hobbies
}.let { id -> stringResource(id) }