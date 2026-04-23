package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.chat.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.domain.feature.secretsanta.model.SecretSantaChatMessage
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.image.RemoteImage
import com.splanes.uoc.wishlify.presentation.common.utils.DateTimePattern
import com.splanes.uoc.wishlify.presentation.common.utils.formatted
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme
import java.util.Date

@Composable
fun ChatMessage(
  message: SecretSantaChatMessage,
  isOtherUserVisible: Boolean,
  modifier: Modifier = Modifier
) {
  val density = LocalDensity.current

  if (message.isCurrentUserMessage) {
    Row(
      modifier = modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.End,
    ) {

      BoxWithConstraints(
        modifier = Modifier
          .fillMaxWidth(fraction = .55f)
          .padding(end = 10.dp),
        contentAlignment = Alignment.CenterEnd,
      ) {
        val minWidth = with(density) { (constraints.maxWidth / 2).toDp() }
        Column(
          modifier = Modifier.widthIn(min = minWidth),
          horizontalAlignment = Alignment.End,
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Text(
            text = message.username(isOtherUserVisible),
            style = WishlifyTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = WishlifyTheme.colorScheme.onPrimaryContainer
          )

          ChatText(
            modifier = Modifier.widthIn(minWidth),
            type = message.type(),
            text = message.text,
            sentAt = message.sentAt
          )
        }
      }

      ChatImage(
        photoUrl = message.sender.photoUrl,
        isAnonymous = false,
      )
    }
  } else {
    Row(
      modifier = modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.Start,
    ) {
      ChatImage(
        photoUrl = message.sender.photoUrl,
        isAnonymous = !isOtherUserVisible,
      )

      BoxWithConstraints(
        modifier = Modifier
          .fillMaxWidth(fraction = .55f)
          .padding(start = 10.dp),
        contentAlignment = Alignment.CenterStart,
      ) {
        val minWidth = with(density) { (constraints.maxWidth / 2).toDp() }
        Column(
          modifier = Modifier.widthIn(min = minWidth),
          horizontalAlignment = Alignment.Start,
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Text(
            text = message.username(isOtherUserVisible),
            style = WishlifyTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = WishlifyTheme.colorScheme.onPrimaryContainer
          )

          ChatText(
            modifier = Modifier.widthIn(minWidth),
            type = message.type(),
            text = message.text,
            sentAt = message.sentAt
          )
        }
      }
    }
  }
}

@Composable
private fun ChatImage(
  photoUrl: String?,
  isAnonymous: Boolean,
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
      isAnonymous -> painterResource(R.drawable.img_anonymous_chat)
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

@Composable
private fun ChatText(
  modifier: Modifier,
  type: MessageType,
  text: String,
  sentAt: Date
) {
  Surface(
    modifier = modifier,
    shape = if (type == MessageType.CurrentUser) {
      WishlifyTheme.shapes.medium.copy(topEnd = CornerSize(0.dp))
    } else {
      WishlifyTheme.shapes.medium.copy(topStart = CornerSize(0.dp))
    },
    color = when (type) {
      MessageType.CurrentUser -> WishlifyTheme.colorScheme.secondaryContainer
      MessageType.OtherUser -> WishlifyTheme.colorScheme.surfaceTint.copy(alpha = .16f)
    }
  ) {
    Column(
      modifier = Modifier.padding(
        top = 8.dp,
        start = 10.dp,
        end = 10.dp
      ),
      horizontalAlignment = if (type == MessageType.CurrentUser) {
        Alignment.End
      } else {
        Alignment.Start
      }
    ) {
      Text(
        text = AnnotatedString.fromHtml(text),
        style = WishlifyTheme.typography.bodyMedium,
        color = WishlifyTheme.colorScheme.onSurface,
        textAlign = if (type == MessageType.CurrentUser) {
          TextAlign.End
        } else {
          TextAlign.Start
        }
      )

      Text(
        modifier = Modifier.padding(vertical = 4.dp),
        text = sentAt.formatted(DateTimePattern.TimeOnly),
        style = WishlifyTheme.typography.labelSmall,
        color = WishlifyTheme.colorScheme.outline
      )
    }
  }
}

private fun SecretSantaChatMessage.type() = when {
  isCurrentUserMessage -> MessageType.CurrentUser
  else -> MessageType.OtherUser
}

@Composable
private fun SecretSantaChatMessage.username(isOtherUserVisible: Boolean) = when {
   !isCurrentUserMessage && !isOtherUserVisible -> stringResource(R.string.anonymous)
  !isCurrentUserMessage -> sender.username
  else -> stringResource(R.string.you)
}

private enum class MessageType {
  CurrentUser,
  OtherUser,
}