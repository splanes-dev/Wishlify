package com.splanes.uoc.wishlify.presentation.feature.groups.feature.search.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
fun SearchUsersQueryResults(
  results: List<User.Basic>,
  added: List<User.Basic>,
  modifier: Modifier = Modifier,
  onAddUser: (User.Basic) -> Unit,
) {
  Column(
    modifier = modifier,
  ) {

    Text(
      text = stringResource(R.string.groups_search_users_results_title),
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
        if (results.isEmpty()) {
          Text(
            text = stringResource(R.string.groups_search_users_results_empty),
            style = WishlifyTheme.typography.bodyMedium,
            color = WishlifyTheme.colorScheme.outline,
          )
        } else {
          results.forEach { result ->
            ResultRow(
              name = result.username,
              code = result.code,
              selected = added.any { u -> u.uid == result.uid },
              onClick = { onAddUser(result) }
            )
          }
        }
      }
    }
  }
}

@Composable
private fun ResultRow(
  name: String,
  code: String,
  selected: Boolean,
  onClick: () -> Unit,
) {

  val contentColor by animateColorAsState(
    targetValue = if (selected) {
      WishlifyTheme.colorScheme.onSuccessContainer
    } else {
      WishlifyTheme.colorScheme.onSurface
    }
  )

  val containerColor by animateColorAsState(
    targetValue = if (selected) {
      WishlifyTheme.colorScheme.successContainer
    } else {
      Color.Transparent
    }
  )

  val contentPadding by animateDpAsState(
    targetValue = if (selected) 8.dp else 0.dp
  )

  Surface(
    color = containerColor,
    shape = WishlifyTheme.shapes.small,
    onClick = onClick,
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(contentPadding),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Icon(
        imageVector = Icons.Outlined.Person,
        contentDescription = name,
        tint = contentColor,
      )

      Row(
        modifier = Modifier.weight(1f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        Text(
          text = name,
          style = WishlifyTheme.typography.bodyMedium,
          color = contentColor,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )

        Text(
          text = "($code)",
          style = WishlifyTheme.typography.labelSmall,
          color = contentColor,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }

      Icon(
        imageVector = if (selected) {
          Icons.Outlined.Check
        } else {
          Icons.Outlined.Add
        },
        contentDescription = stringResource(R.string.add),
        tint = contentColor,
      )
    }
  }
}