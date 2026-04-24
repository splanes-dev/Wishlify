package com.splanes.uoc.wishlify.presentation.feature.shared.feature.detail.components

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
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlist
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.CardImage
import com.splanes.uoc.wishlify.presentation.common.components.image.ImageOrPlaceholder
import com.splanes.uoc.wishlify.presentation.common.utils.formatted
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharedWishlistInfoBottomSheet(
  visible: Boolean,
  sheetState: SheetState,
  wishlist: SharedWishlist,
  modifier: Modifier = Modifier,
  onDismiss: () -> Unit,
) {

  if (visible) {

    val density = LocalDensity.current
    var imageSize by remember { mutableStateOf(145.dp) }
    val group = wishlist.group
    val participantsCount = wishlist.participants.count()

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
            CardImage(
              modifier = Modifier.clip(WishlifyTheme.shapes.small),
              width = imageSize,
              media = wishlist.linkedWishlist.photo
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
              text = wishlist.linkedWishlist.name,
              style = WishlifyTheme.typography.titleLarge,
              color = WishlifyTheme.colorScheme.onSurface,
              fontWeight = FontWeight.Bold
            )

            wishlist.linkedWishlist.target?.let { target ->
              Text(
                text = target,
                style = WishlifyTheme.typography.titleSmall,
                color = WishlifyTheme.colorScheme.onSurface
              )
            }

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
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  Icon(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(R.drawable.ic_gift),
                    contentDescription = null,
                    tint = WishlifyTheme.colorScheme.secondary,
                  )

                  Text(
                    text = pluralStringResource(
                      R.plurals.wishlists_list_item_count,
                      wishlist.numOfItems,
                      wishlist.numOfItems
                    ),
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
                    text = wishlist.deadline.formatted(),
                    style = WishlifyTheme.typography.bodyMedium,
                    color = WishlifyTheme.colorScheme.secondary
                  )
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        ParticipantsList(wishlist)

        Spacer(modifier = Modifier.height(16.dp))

        Surface(
          modifier = Modifier.fillMaxWidth(),
          shape = WishlifyTheme.shapes.small,
          color = WishlifyTheme.colorScheme.surfaceContainerHigh
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(8.dp)
          ) {
            Text(
              text = stringResource(R.string.wishlists_item_description_or_notes),
              style = WishlifyTheme.typography.labelSmall,
              color = WishlifyTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(4.dp))

            Text(
              modifier = Modifier.padding(start = 8.dp),
              text = wishlist.linkedWishlist.description.takeUnless { it.isBlank() }
                ?: stringResource(R.string.wishlists_item_description_or_notes_empty),
              style = WishlifyTheme.typography.bodyMedium,
              color = WishlifyTheme.colorScheme.onSurface.let { color ->
                if (wishlist.linkedWishlist.description.isBlank()) {
                  color.copy(alpha = .6f)
                } else {
                  color
                }
              },
              textAlign = TextAlign.Justify
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))
      }
    }
  }
}

@Composable
private fun ParticipantsList(wishlist: SharedWishlist) {

  Text(
    text = stringResource(R.string.participants),
    style = WishlifyTheme.typography.titleMedium,
    color = WishlifyTheme.colorScheme.onSurface
  )

  wishlist.group?.let { group ->
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

  wishlist.participants.forEach { participant ->
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