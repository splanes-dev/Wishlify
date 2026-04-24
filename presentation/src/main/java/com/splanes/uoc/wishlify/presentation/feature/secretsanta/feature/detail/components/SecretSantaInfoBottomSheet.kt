package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EventBusy
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.rounded.EventAvailable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.domain.feature.secretsanta.model.SecretSantaEventDetail
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.image.ImageOrPlaceholder
import com.splanes.uoc.wishlify.presentation.common.utils.formatted
import com.splanes.uoc.wishlify.presentation.common.utils.isExpired
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.utils.formatPrice
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecretSantaInfoBottomSheet(
  visible: Boolean,
  sheetState: SheetState,
  event: SecretSantaEventDetail,
  modifier: Modifier = Modifier,
  onDismiss: () -> Unit,
) {
  if (visible) {

    val density = LocalDensity.current
    var imageSize by remember { mutableStateOf(145.dp) }
    val group = event.group
    val participantsCount = event.participants.count()

    ModalBottomSheet(
      modifier = modifier,
      sheetState = sheetState,
      onDismissRequest = onDismiss
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
      ) {

        Row {
          Box(modifier = Modifier.size(imageSize)) {
            ImageOrPlaceholder(
              modifier = Modifier
                .size(imageSize)
                .clip(WishlifyTheme.shapes.small),
              url = event.photoUrl,
              placeholder = painterResource(R.drawable.img_secret_santa_event_placeholder),
              shape = WishlifyTheme.shapes.small
            )
          }

          Spacer(modifier = Modifier.width(16.dp))

          Column(
            modifier = Modifier.onGloballyPositioned { coordinates ->
              val height = coordinates.size.height
              imageSize = with(density) { height.toDp() }
            },
            verticalArrangement = Arrangement.spacedBy(2.dp)
          ) {
            Text(
              modifier = Modifier.fillMaxWidth(),
              text = event.name,
              style = WishlifyTheme.typography.titleLarge,
              color = WishlifyTheme.colorScheme.onSurface,
              fontWeight = FontWeight.Bold
            )

            Surface(
              modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
              color = WishlifyTheme.colorScheme.surfaceContainerHigh,
              shape = WishlifyTheme.shapes.small
            ) {
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                ) {
                  Icon(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(R.drawable.ic_money_bag),
                    contentDescription = null,
                    tint = WishlifyTheme.colorScheme.secondary,
                  )

                  Spacer(Modifier.width(8.dp))

                  Text(
                    text = event.budget.toFloat().formatPrice(),
                    style = WishlifyTheme.typography.bodyMedium,
                    color = WishlifyTheme.colorScheme.secondary
                  )

                  if (event.isBudgetApproximate) {
                    Spacer(Modifier.width(2.dp))

                    Text(
                      text = stringResource(R.string.secret_santa_event_detail_header_budget_approx),
                      style = WishlifyTheme.typography.bodySmall,
                      color = WishlifyTheme.colorScheme.secondary
                    )
                  }
                }

                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  Icon(
                    modifier = Modifier.size(20.dp),
                    imageVector = Icons.Outlined.Group,
                    contentDescription = null,
                    tint = WishlifyTheme.colorScheme.secondary,
                  )

                  Text(
                    text = when {
                      group != null && participantsCount != 0 ->
                        stringResource(
                          R.string.shared_wishlists_detail_participants_header,
                          group.membersCount + participantsCount
                        )

                      group != null -> group.name
                      else -> stringResource(
                        R.string.shared_wishlists_detail_participants_header,
                        participantsCount
                      )
                    },
                    style = WishlifyTheme.typography.bodyMedium,
                    color = WishlifyTheme.colorScheme.secondary
                  )
                }

                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  Icon(
                    modifier = Modifier.size(20.dp),
                    imageVector = Icons.Rounded.EventAvailable,
                    contentDescription = null,
                    tint = WishlifyTheme.colorScheme.secondary,
                  )

                  Text(
                    text = event.deadline.formatted(),
                    style = WishlifyTheme.typography.bodyMedium,
                    color = WishlifyTheme.colorScheme.secondary
                  )
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        ParticipantsList(event)

        Spacer(modifier = Modifier.height(16.dp))

        DrawInfo(event)

        Spacer(modifier = Modifier.height(16.dp))
      }
    }
  }
}

@Composable
private fun ParticipantsList(event: SecretSantaEventDetail) {

  Text(
    text = stringResource(R.string.participants),
    style = WishlifyTheme.typography.titleMedium,
    color = WishlifyTheme.colorScheme.onSurface
  )

  event.group?.let { group ->
    Spacer(Modifier.height(8.dp))

    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      ImageOrPlaceholder(
        modifier = Modifier
          .size(40.dp)
          .clip(WishlifyTheme.shapes.small),
        url = group.photoUrl,
        placeholder = painterResource(R.drawable.preset_group),
        shape = WishlifyTheme.shapes.small
      )

      Text(
        text = group.name,
        style = WishlifyTheme.typography.bodyMedium,
        color = WishlifyTheme.colorScheme.onSurface
      )

      Text(
        text = "(${group.membersCount} membres)",
        style = WishlifyTheme.typography.labelMedium,
        color = WishlifyTheme.colorScheme.onSurface
      )
    }
  }

  Spacer(Modifier.height(8.dp))

  event.participants.forEach { participant ->
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      ImageOrPlaceholder(
        modifier = Modifier
          .size(40.dp)
          .clip(WishlifyTheme.shapes.small),
        url = participant.photoUrl,
        placeholder = painterResource(R.drawable.img_placeholder_avatar),
        shape = WishlifyTheme.shapes.small
      )

      Text(
        text = participant.username,
        style = WishlifyTheme.typography.bodyMedium,
        color = WishlifyTheme.colorScheme.onSurface
      )
    }

    Spacer(Modifier.height(4.dp))
  }
}

@Composable
private fun DrawInfo(event: SecretSantaEventDetail) {
  if (event.deadline.isExpired()) {
    Surface(
      modifier = Modifier.fillMaxWidth(),
      color = WishlifyTheme.colorScheme.errorContainer,
      shape = WishlifyTheme.shapes.small
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Icon(
          modifier = Modifier.size(20.dp),
          imageVector = Icons.Outlined.EventBusy,
          contentDescription = null,
          tint = WishlifyTheme.colorScheme.onErrorContainer
        )

        Text(
          text = stringResource(R.string.event_finished),
          style = WishlifyTheme.typography.titleSmall,
          color = WishlifyTheme.colorScheme.onErrorContainer,
          fontWeight = FontWeight.Bold
        )
      }
    }
  } else {
    when (event) {
      is SecretSantaEventDetail.DrawDone -> {
        Surface(
          modifier = Modifier.fillMaxWidth(),
          color = WishlifyTheme.colorScheme.infoContainer,
          shape = WishlifyTheme.shapes.small
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Text(
              text = stringResource(R.string.secret_santa_draw_done),
              style = WishlifyTheme.typography.titleMedium,
              color = WishlifyTheme.colorScheme.onInfoContainer
            )

            Text(
              text = stringResource(R.string.secret_santa_event_detail_draw_done_receiver_title),
              style = WishlifyTheme.typography.titleSmall,
              color = WishlifyTheme.colorScheme.onInfoContainer
            )

            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              ImageOrPlaceholder(
                modifier = Modifier
                  .size(40.dp)
                  .clip(WishlifyTheme.shapes.small),
                url = event.receiver.photoUrl,
                placeholder = painterResource(R.drawable.img_placeholder_avatar),
                shape = WishlifyTheme.shapes.small
              )

              Text(
                text = event.receiver.username,
                style = WishlifyTheme.typography.bodyMedium,
                color = WishlifyTheme.colorScheme.onSurface
              )
            }
          }

        }
      }

      is SecretSantaEventDetail.DrawPending ->
        Surface(
          modifier = Modifier.fillMaxWidth(),
          color = WishlifyTheme.colorScheme.warningContainer,
          shape = WishlifyTheme.shapes.small
        ) {
          Text(
            modifier = Modifier.padding(8.dp),
            text = stringResource(R.string.secret_santa_draw_pending),
            style = WishlifyTheme.typography.titleMedium,
            color = WishlifyTheme.colorScheme.onWarningContainer
          )
        }
    }
  }
}