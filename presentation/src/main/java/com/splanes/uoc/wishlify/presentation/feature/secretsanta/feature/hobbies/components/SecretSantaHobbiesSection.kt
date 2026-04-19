package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.hobbies.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.domain.feature.user.model.User
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.image.ImageOrPlaceholder
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@Composable
fun SecretSantaHobbiesSection(
  user: User.HobbiesProfile,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier,
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Text(
      text = stringResource(R.string.secret_santa_hobbies_section_title),
      style = WishlifyTheme.typography.titleLarge,
      color = WishlifyTheme.colorScheme.onSurface
    )

    Text(
      text = stringResource(R.string.secret_santa_hobbies_section_description),
      style = WishlifyTheme.typography.bodyMedium,
      color = WishlifyTheme.colorScheme.onSurface,
      textAlign = TextAlign.Justify
    )

    UserRow(
      photoUrl = user.photoUrl,
      username = user.username
    )

    if (user.hobbies.values.isNotEmpty()) {
      FlowRow(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        user.hobbies.values.forEach { hobby -> Hobby(text = hobby) }
      }
    } else {
      Text(
        text = stringResource(R.string.secret_santa_hobbies_section_empty_hobbies),
        style = WishlifyTheme.typography.bodySmall,
        color = WishlifyTheme.colorScheme.outline,
      )
    }

    HorizontalDivider(
      modifier = Modifier.fillMaxWidth(),
      color = WishlifyTheme.colorScheme.outline
    )
  }
}

@Composable
private fun UserRow(
  photoUrl: String?,
  username: String
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    ImageOrPlaceholder(
      modifier = Modifier
        .size(36.dp)
        .border(
          width = 1.dp,
          color = WishlifyTheme.colorScheme.outline.copy(alpha = .16f),
          shape = WishlifyTheme.shapes.small
        ),
      shape = WishlifyTheme.shapes.small,
      url = photoUrl,
      placeholder = painterResource(R.drawable.img_placeholder_avatar),
    )

    Text(
      text = username,
      style = WishlifyTheme.typography.headlineSmall,
      color = WishlifyTheme.colorScheme.onSurface
    )
  }
}

@Composable
private fun Hobby(text: String) {
  Surface(
    shape = WishlifyTheme.shapes.small,
    color = WishlifyTheme.colorScheme.surfaceContainerHigh
  ) {
    Row(
      modifier = Modifier.padding(8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Text(
        text = text,
        style = WishlifyTheme.typography.labelLarge,
        color = WishlifyTheme.colorScheme.onSurface,
      )
    }
  }
}