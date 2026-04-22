package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.detail.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.domain.feature.secretsanta.model.SecretSantaEventDetail
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonShape
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonText
import com.splanes.uoc.wishlify.presentation.common.components.image.RemoteImage
import com.splanes.uoc.wishlify.presentation.common.utils.htmlString
import com.splanes.uoc.wishlify.presentation.common.utils.isExpired
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.detail.model.SecretSantaDetailAction
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@Composable
fun SecretSantaDrawDoneDetailContent(
  event: SecretSantaEventDetail.DrawDone,
  modifier: Modifier = Modifier,
  onAction: (SecretSantaDetailAction.DrawDone) -> Unit,
) {
  Column(modifier = modifier) {

    ReceiverSection(
      event = event,
      onAction = onAction
    )

    Spacer(Modifier.height(16.dp))

    GiverSection(
      event = event,
      onAction = onAction
    )
  }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ReceiverSection(
  event: SecretSantaEventDetail.DrawDone,
  onAction: (SecretSantaDetailAction.DrawDone) -> Unit,
) {

  Text(
    text = stringResource(R.string.secret_santa_event_detail_draw_done_receiver_title),
    style = WishlifyTheme.typography.titleLarge,
    color = WishlifyTheme.colorScheme.onSurface,
  )

  Spacer(Modifier.height(16.dp))

  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    UserAvatar(photoUrl = event.receiver.photoUrl)

    Text(
      text = event.receiver.username,
      style = WishlifyTheme.typography.headlineSmall,
      color = WishlifyTheme.colorScheme.onSurface,
      maxLines = 2,
      overflow = TextOverflow.Ellipsis
    )
  }

  Spacer(Modifier.height(16.dp))

  if (event.receiverSharedHobbies || event.receiverSharedWishlist != null) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      if (event.receiverSharedHobbies) {
        OutlinedButton(
          modifier = Modifier.weight(1f),
          shapes = ButtonShape,
          onClick = { onAction(SecretSantaDetailAction.SeeReceiverHobbies) }
        ) {
          ButtonText(
            text = stringResource(R.string.secret_santa_event_detail_draw_done_see_receiver_interests),
            style = WishlifyTheme.typography.labelLarge
          )
        }
      } else {
        Spacer(Modifier.weight(1f))
      }

      event.receiverSharedWishlist?.let { wishlist ->
        Button(
          modifier = Modifier.weight(1f),
          shapes = ButtonShape,
          onClick = { onAction(SecretSantaDetailAction.SeeReceiverWishlist(wishlist)) }
        ) {
          ButtonText(
            text = stringResource(R.string.secret_santa_event_detail_draw_done_see_receiver_wishlist),
            style = WishlifyTheme.typography.labelLarge
          )
        }
      } ?: Spacer(Modifier.weight(1f))
    }

    Spacer(Modifier.height(16.dp))
  }

  AnonymousChat(
    modifier = Modifier.fillMaxWidth(),
    enabled = !event.deadline.isExpired(),
    text = htmlString(R.string.secret_santa_event_detail_draw_done_receiver_anonymous_chat_description, event.receiver.username),
    pendingNotifications = event.receiverChatNotificationCount,
    onClick = { onAction(SecretSantaDetailAction.OpenReceiverChat) }
  )

  Spacer(Modifier.height(16.dp))

  HorizontalDivider(
    modifier = Modifier.fillMaxWidth(),
    color = WishlifyTheme.colorScheme.outline.copy(alpha = .33f)
  )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun GiverSection(
  event: SecretSantaEventDetail.DrawDone,
  onAction: (SecretSantaDetailAction.DrawDone) -> Unit,
) {
  Text(
    text = stringResource(R.string.secret_santa_event_detail_draw_done_giver_title),
    style = WishlifyTheme.typography.titleLarge,
    color = WishlifyTheme.colorScheme.onSurface,
  )

  Spacer(Modifier.height(16.dp))

  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(10.dp)
  ) {

    val wishlist = event.currentUserSharedWishlist

    if (wishlist == null) {
      Text(
        modifier = Modifier.weight(1f),
        text = stringResource(R.string.secret_santa_event_detail_draw_done_giver_share_wishlist_description),
        style = WishlifyTheme.typography.bodyMedium,
        color = WishlifyTheme.colorScheme.onSurface
      )

      Button(
        modifier = Modifier.weight(1f),
        shapes = ButtonShape,
        enabled = !event.deadline.isExpired(),
        onClick = { onAction(SecretSantaDetailAction.ShareGiverWishlist) }
      ) {
        ButtonText(
          text = stringResource(R.string.secret_santa_event_detail_draw_done_giver_share_wishlist_btn),
          style = WishlifyTheme.typography.labelLarge
        )
      }
    } else {
      GiverWishlistSharedBanner(modifier = Modifier.weight(1f))

      OutlinedButton(
        shapes = ButtonShape,
        border = BorderStroke(width = 1.dp, color = WishlifyTheme.colorScheme.primary),
        colors = ButtonDefaults.outlinedButtonColors(
          contentColor = WishlifyTheme.colorScheme.primary
        ),
        onClick = { onAction(SecretSantaDetailAction.SeeGiverWishlist(wishlist)) }
      ) {
        ButtonText(
          text = stringResource(R.string.secret_santa_event_detail_draw_done_giver_share_wishlist_shared_see_wishlist),
          style = WishlifyTheme.typography.labelLarge
        )
      }
    }
  }

  Spacer(Modifier.height(16.dp))

  AnonymousChat(
    modifier = Modifier.fillMaxWidth(),
    enabled = !event.deadline.isExpired(),
    text = htmlString(R.string.secret_santa_event_detail_draw_done_giver_anonymous_chat_description),
    pendingNotifications = event.giverChatNotificationCount,
    onClick = { onAction(SecretSantaDetailAction.OpenGiverChat) }
  )

  Spacer(Modifier.height(16.dp))
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun AnonymousChat(
  text: AnnotatedString,
  enabled: Boolean,
  pendingNotifications: Int,
  modifier: Modifier = Modifier,
  onClick: () -> Unit,
) {
  Surface(
    modifier = modifier,
    shape = WishlifyTheme.shapes.extraSmall,
    border = BorderStroke(1.dp, color = WishlifyTheme.colorScheme.warning),
    color = Color.Transparent,
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Text(
        modifier = Modifier.weight(.7f),
        text = text,
        style = WishlifyTheme.typography.bodyMedium,
        color = WishlifyTheme.colorScheme.onSurface
      )

      Box(
        modifier = Modifier.weight(.3f),
        contentAlignment = Alignment.Center
      ) {
        // TODO: Notification Badge
        Button(
          modifier = Modifier.fillMaxWidth(),
          shapes = ButtonShape,
          enabled = enabled,
          colors = ButtonDefaults.buttonColors(
            containerColor = WishlifyTheme.colorScheme.tertiaryContainer,
            contentColor = WishlifyTheme.colorScheme.onTertiaryContainer,
          ),
          onClick = onClick
        ) {
          ButtonText(
            text = stringResource(R.string.chat_anonymous),
            style = WishlifyTheme.typography.labelLarge
          )
        }
      }
    }
  }
}

@Composable
private fun GiverWishlistSharedBanner(
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier,
    shape = WishlifyTheme.shapes.small,
    color = WishlifyTheme.colorScheme.successContainer
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Icon(
        imageVector = Icons.Outlined.Verified,
        contentDescription = null,
        tint = WishlifyTheme.colorScheme.onSuccessContainer
      )

      Text(
        text = htmlString(R.string.secret_santa_event_detail_draw_done_giver_share_wishlist_shared_banner),
        style = WishlifyTheme.typography.bodyMedium,
        color = WishlifyTheme.colorScheme.onSuccessContainer
      )
    }
  }
}

@Composable
private fun UserAvatar(
  photoUrl: String?,
  modifier: Modifier = Modifier,
) {
  Box(
    modifier = modifier
      .size(56.dp)
      .clip(WishlifyTheme.shapes.small)
      .border(
        width = 1.dp,
        color = WishlifyTheme.colorScheme.outline.copy(alpha = .16f),
        shape = WishlifyTheme.shapes.small
      )
  ) {
    val painter = when {
      photoUrl.isNullOrBlank() -> painterResource(R.drawable.img_placeholder_avatar)
      else -> null
    }

    if (painter != null) {
      Image(
        modifier = Modifier.fillMaxSize(),
        painter = painter,
        contentDescription = null,
        contentScale = ContentScale.Crop
      )
    } else {
      RemoteImage(
        modifier = Modifier.fillMaxSize(),
        url = photoUrl,
        contentDescription = null,
        contentScale = ContentScale.Crop,
      )
    }
  }
}