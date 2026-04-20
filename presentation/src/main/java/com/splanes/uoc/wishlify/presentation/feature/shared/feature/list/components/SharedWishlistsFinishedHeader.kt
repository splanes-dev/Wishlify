package com.splanes.uoc.wishlify.presentation.feature.shared.feature.list.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.EventBusy
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.utils.htmlString
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@Composable
fun SharedWishlistsFinishedHeader(
  isVisible: Boolean,
  description: AnnotatedString,
  modifier: Modifier = Modifier,
  onChangeVisibility: () -> Unit,
) {

  val rotation by animateFloatAsState(targetValue = if (isVisible) -90f else 90f)

  Surface(
    modifier = modifier,
    shape = WishlifyTheme.shapes.small,
    color = WishlifyTheme.colorScheme.error.copy(alpha = .7f),
    onClick = onChangeVisibility
  ) {

    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp),
      verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {

      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Icon(
          modifier = Modifier.size(20.dp),
          imageVector = Icons.Outlined.EventBusy,
          contentDescription = null,
          tint = WishlifyTheme.colorScheme.onError
        )

        Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text(
            text = htmlString(R.string.shared_wishlists_finished),
            style = WishlifyTheme.typography.titleSmall,
            color = WishlifyTheme.colorScheme.onError,
            fontWeight = FontWeight.Bold
          )
        }

        Text(
          text = if (isVisible) {
            stringResource(R.string.shared_wishlists_finished_hide)
          } else {
            stringResource(R.string.shared_wishlists_finished_show)
          },
          style = WishlifyTheme.typography.bodySmall,
          color = WishlifyTheme.colorScheme.onError,
        )

        Icon(
          modifier = Modifier
            .size(20.dp)
            .rotate(rotation),
          imageVector = Icons.Outlined.ChevronRight,
          contentDescription = null,
          tint = WishlifyTheme.colorScheme.onError
        )
      }

      Text(
        modifier = Modifier
          .fillMaxWidth()
          .padding(
            start = 28.dp,
            end = 60.dp
          ),
        text = description,
        style = WishlifyTheme.typography.bodySmall,
        color = WishlifyTheme.colorScheme.onError,
      )
    }
  }
}