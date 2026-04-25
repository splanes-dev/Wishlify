package com.splanes.uoc.wishlify.presentation.feature.groups.feature.list.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PersonSearch
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.domain.feature.user.model.User
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@Composable
fun GroupMemberPicker(
  users: List<User.Basic>,
  modifier: Modifier = Modifier,
  onRemoveUser: (User.Basic) -> Unit,
  onSearchUser: () -> Unit
) {
  Surface(
    modifier = modifier,
    shape = WishlifyTheme.shapes.small,
    color = WishlifyTheme.colorScheme.surfaceContainer
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(
          vertical = 8.dp,
          horizontal = 12.dp
        )
    ) {
      Header(onSearchClick = onSearchUser)

      Spacer(Modifier.height(4.dp))

      Crossfade(users) { u ->
        if (u.isEmpty()) {

          Text(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 12.dp),
            text = stringResource(R.string.groups_new_group_members_input_empty_description),
            style = WishlifyTheme.typography.bodyMedium,
            color = WishlifyTheme.colorScheme.outline,
            textAlign = TextAlign.Center,
          )

        } else {

          Spacer(Modifier.height(4.dp))

          Column {
            u.forEachIndexed { index, user ->
              UserRow(
                username = user.username,
                onRemove = { onRemoveUser(user) }
              )

              if (index != u.lastIndex) {
                Spacer(Modifier.height(4.dp))
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun Header(onSearchClick: () -> Unit) {
  Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(4.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Icon(
        imageVector = Icons.Outlined.PersonSearch,
        contentDescription = stringResource(R.string.groups_new_group_members_input_label),
        tint = WishlifyTheme.colorScheme.onSurfaceVariant
      )

      Text(
        text = stringResource(R.string.groups_new_group_members_input_label),
        style =  WishlifyTheme.typography.bodyLarge,
        color =  WishlifyTheme.colorScheme.onSurfaceVariant,
      )

      Spacer(Modifier.weight(1f))

      Surface(
        shape = WishlifyTheme.shapes.small,
        color = Color.Transparent,
        onClick = onSearchClick
      ) {
        Icon(
          modifier = Modifier.padding(4.dp),
          imageVector = Icons.Outlined.Search,
          contentDescription = stringResource(R.string.groups_search_users),
          tint = WishlifyTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    HorizontalDivider(
      modifier = Modifier.fillMaxWidth(),
      color = WishlifyTheme.colorScheme.outline.copy(alpha = .5f)
    )
  }
}


@Composable
private fun UserRow(
  username: String,
  onRemove: () -> Unit
) {
  Surface(
    shape = WishlifyTheme.shapes.extraSmall,
    color = WishlifyTheme.colorScheme.surfaceVariant
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(start = 8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = Icons.Outlined.Person,
        contentDescription = username,
        tint = WishlifyTheme.colorScheme.onSurface
      )

      Spacer(Modifier.width(12.dp))

      Text(
        text = username,
        style = WishlifyTheme.typography.bodyMedium,
        color = WishlifyTheme.colorScheme.onSurface,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )

      Spacer(Modifier.weight(1f))

      Surface(
        shape = WishlifyTheme.shapes.small,
        color = Color.Transparent,
        onClick = onRemove
      ) {
        Icon(
          modifier = Modifier.padding(4.dp),
          imageVector = Icons.Outlined.Close,
          contentDescription = stringResource(R.string.groups_search_users_remove),
          tint = WishlifyTheme.colorScheme.onSurface
        )
      }
    }
  }
}