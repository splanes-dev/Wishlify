package com.splanes.uoc.wishlify.presentation.feature.groups.feature.search.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.domain.feature.user.model.User
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@Composable
fun SearchUsersResultsAdded(
  added: List<User.Basic>,
  modifier: Modifier = Modifier,
  onRemoveUser: (User.Basic) -> Unit,
) {
  Column(
    modifier = modifier,
  ) {

    Text(
      text = stringResource(R.string.groups_search_users_results_added_title),
      style = WishlifyTheme.typography.bodyLarge,
      color = WishlifyTheme.colorScheme.onSurface,
    )

    Spacer(Modifier.height(12.dp))

    Surface(
      color = WishlifyTheme.colorScheme.surfaceContainer,
      shape = WishlifyTheme.shapes.small,
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        added.forEach { result ->
          ResultAddedRow(
            name = result.username,
            code = result.code,
            onClick = { onRemoveUser(result) }
          )
        }
      }
    }
  }
}

@Composable
private fun ResultAddedRow(
  name: String,
  code: String,
  onClick: () -> Unit,
) {
  Surface(
    color = Color.Transparent,
    shape = WishlifyTheme.shapes.small,
    onClick = onClick,
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(4.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Icon(
        imageVector = Icons.Outlined.Person,
        contentDescription = name,
        tint = WishlifyTheme.colorScheme.onSurface,
      )

      Row(
        modifier = Modifier.weight(1f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        Text(
          text = name,
          style = WishlifyTheme.typography.bodyMedium,
          color = WishlifyTheme.colorScheme.onSurface,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )

        Text(
          text = "($code)",
          style = WishlifyTheme.typography.labelSmall,
          color = WishlifyTheme.colorScheme.onSurface,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }

      Icon(
        imageVector = Icons.Outlined.Close,
        contentDescription = stringResource(R.string.delete),
        tint = WishlifyTheme.colorScheme.onSurface,
      )
    }
  }
}