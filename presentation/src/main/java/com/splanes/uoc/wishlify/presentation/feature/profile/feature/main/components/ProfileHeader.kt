package com.splanes.uoc.wishlify.presentation.feature.profile.feature.main.components

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.domain.feature.user.model.User
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.button.IconButtonShape
import com.splanes.uoc.wishlify.presentation.common.components.image.ImageOrPlaceholder
import com.splanes.uoc.wishlify.presentation.common.utils.copyToClipboard
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme
import kotlinx.coroutines.launch

@Composable
fun ProfileHeader(
  user: User.BasicProfile,
  modifier: Modifier = Modifier
) {

  val coroutineScope = rememberCoroutineScope()
  val clipboard = LocalClipboard.current
  val context = LocalContext.current
  val resources = LocalResources.current

  Row(
    modifier = modifier.height(125.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    Box(
      modifier = Modifier
        .width(145.dp)
        .fillMaxHeight()
        .border(
          width = 1.dp,
          color = WishlifyTheme.colorScheme.outline.copy(alpha = .3f),
          shape = WishlifyTheme.shapes.small
        )
    ) {
      ImageOrPlaceholder(
        modifier = Modifier
          .fillMaxSize()
          .padding(4.dp),
        shape = WishlifyTheme.shapes.small,
        url = user.photoUrl,
        placeholder = painterResource(R.drawable.img_placeholder_avatar),
        contenScale = ContentScale.Fit
      )
    }

    Column(
      modifier = Modifier.weight(1f),
      verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      Text(
        text = user.username,
        style = WishlifyTheme.typography.headlineMedium,
        color = WishlifyTheme.colorScheme.onSurface,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )

      UserPoints(user.points)

      Spacer(Modifier.weight(1f))

      UserCode(
        code = user.code,
        onClick = {
          coroutineScope.launch {
            clipboard.copyToClipboard(
              label = resources.getString(R.string.groups_member_code),
              text = user.code
            )
            Toast.makeText(
              context,
              R.string.groups_member_code_clipboard_copied,
              Toast.LENGTH_SHORT
            ).show()
          }
        }
      )
    }
  }
}

@Composable
private fun UserPoints(points: Int) {
  Surface(
    shape = WishlifyTheme.shapes.small,
    color = WishlifyTheme.colorScheme.warningContainer
  ) {
    Row(
      modifier = Modifier
        .padding(
          vertical = 4.dp,
          horizontal = 8.dp
        ),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      Icon(
        painter = painterResource(R.drawable.ic_points),
        contentDescription = null,
        tint = WishlifyTheme.colorScheme.onWarningContainer,
      )

      Text(
        text = stringResource(R.string.profile_user_points, points),
        style = WishlifyTheme.typography.titleLarge,
        color = WishlifyTheme.colorScheme.onWarningContainer,
      )
    }
  }
}

@Composable
private fun UserCode(
  code: String,
  onClick: () -> Unit,
) {
  Surface(
    modifier = Modifier.fillMaxWidth(),
    shape = WishlifyTheme.shapes.small,
    color = Color.Transparent,
    onClick = onClick
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(
        text = code,
        style = WishlifyTheme.typography.titleMedium,
        color = WishlifyTheme.colorScheme.onSurface
      )

      IconButton(
        shapes = IconButtonShape,
        colors = IconButtonDefaults.iconButtonColors(
          containerColor = WishlifyTheme.colorScheme.surfaceVariant,
          contentColor = WishlifyTheme.colorScheme.onSurfaceVariant
        ),
        onClick = onClick
      ) {
        Icon(
          imageVector = Icons.Rounded.ContentCopy,
          contentDescription = null,
        )
      }
    }
  }
}