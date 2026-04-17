package com.splanes.uoc.wishlify.presentation.feature.groups.feature.detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.domain.feature.groups.model.Group
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonShape
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonText
import com.splanes.uoc.wishlify.presentation.feature.groups.components.GroupStateLabel
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun GroupDetailHeader(
  group: Group.Detail,
  modifier: Modifier = Modifier,
  onSharedWishlistsClick: () -> Unit,
  onSecretSantaClick: () -> Unit,
) {

  Column(
    modifier = modifier.background(color = WishlifyTheme.colorScheme.surface),
    verticalArrangement = Arrangement.spacedBy(4.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Icon(
        modifier = Modifier.size(24.dp),
        imageVector = Icons.Outlined.Group,
        contentDescription = null,
        tint = WishlifyTheme.colorScheme.secondary
      )

      Spacer(Modifier.width(8.dp))

      Text(
        text = stringResource(R.string.groups_list_member_count, group.membersCount),
        style = WishlifyTheme.typography.titleMedium,
        color = WishlifyTheme.colorScheme.secondary
      )

      Spacer(Modifier.weight(1f))

      GroupStateLabel(
        state = group.state,
        size = GroupStateLabel.Large
      )
    }

    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Button(
        modifier = Modifier.weight(1f),
        shapes = ButtonShape,
        enabled = group.hasSharedWishlists,
        onClick = onSharedWishlistsClick
      ) {
        ButtonText(
          text = stringResource(R.string.groups_see_group_shared_wishlists),
          style = WishlifyTheme.typography.labelLarge
        )
      }

      Button(
        modifier = Modifier.weight(1f),
        shapes = ButtonShape,
        enabled = group.hasSecretSantaEvents,
        onClick = onSecretSantaClick
      ) {
        ButtonText(
          text = stringResource(R.string.groups_see_group_secret_santa_events),
          style = WishlifyTheme.typography.labelLarge
        )
      }
    }

    HorizontalDivider(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 4.dp),
      color = WishlifyTheme.colorScheme.secondary
    )
  }
}