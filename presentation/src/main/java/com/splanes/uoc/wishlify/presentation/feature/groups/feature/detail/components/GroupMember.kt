package com.splanes.uoc.wishlify.presentation.feature.groups.feature.detail.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.domain.feature.user.model.User
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.image.ImageOrPlaceholder
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@Composable
fun GroupMember(
  user: User.Basic,
  isCurrentUser: Boolean,
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
      modifier = Modifier.padding(4.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {

      ImageOrPlaceholder(
        modifier = Modifier
          .size(80.dp)
          .border(
            width = 1.dp,
            color = WishlifyTheme.colorScheme.outline.copy(alpha = .16f),
            shape = WishlifyTheme.shapes.small
          ),
        shape = WishlifyTheme.shapes.small,
        url = user.photoUrl,
        placeholder = painterResource(R.drawable.img_placeholder_avatar),
        contentDescription = user.username
      )

      Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        Text(
          text = if (isCurrentUser) {
            stringResource(R.string.groups_member_you, user.username)
          } else {
            user.username
          },
          style = WishlifyTheme.typography.bodyLarge,
          color = WishlifyTheme.colorScheme.onSurface,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )

        Text(
          text = user.code,
          style = WishlifyTheme.typography.bodyLarge,
          color = WishlifyTheme.colorScheme.outline,
        )
      }
    }
  }

}