package com.splanes.uoc.wishlify.presentation.feature.wishlists.components

import android.widget.Toast
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
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.EventAvailable
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.Sell
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.domain.common.model.InviteLink
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Category
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Wishlist
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.CardImage
import com.splanes.uoc.wishlify.presentation.common.components.input.text.TextInput
import com.splanes.uoc.wishlify.presentation.common.components.input.text.rememberTextInputState
import com.splanes.uoc.wishlify.presentation.common.utils.copyToClipboard
import com.splanes.uoc.wishlify.presentation.common.utils.formatted
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistInfoBottomSheet(
  visible: Boolean,
  sheetState: SheetState,
  wishlist: Wishlist,
  modifier: Modifier = Modifier,
  onDismiss: () -> Unit,
) {

  if (visible) {

    val density = LocalDensity.current
    var imageSize by remember { mutableStateOf(145.dp) }

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
              media = wishlist.photo
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
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                modifier = Modifier.weight(1f),
                text = wishlist.title,
                style = WishlifyTheme.typography.titleLarge,
                color = WishlifyTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
              )

              wishlist.category?.let { category ->
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                  Text(
                    text = category.category.name,
                    style = WishlifyTheme.typography.titleSmall,
                    color = WishlifyTheme.colorScheme.onSurface
                  )

                  Icon(
                    modifier = Modifier.size(20.dp),
                    imageVector = Icons.Rounded.Sell,
                    contentDescription = stringResource(R.string.wishlists_new_list_third_party_input),
                    tint = category.category.color.color(),
                  )
                }
              }
            }

            if (wishlist is Wishlist.ThirdParty) {
              Text(
                text = wishlist.target,
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
                    painter = painterResource(R.drawable.ic_person_edit),
                    contentDescription = null,
                    tint = WishlifyTheme.colorScheme.secondary,
                  )

                  Text(
                    text = pluralStringResource(
                      R.plurals.wishlists_list_editors,
                      wishlist.editors.count(),
                      wishlist.editors.count()
                    ),
                    style = WishlifyTheme.typography.bodyMedium,
                    color = WishlifyTheme.colorScheme.secondary
                  )

                  if (wishlist.editors.count() > 1) {
                    Text(
                      text = "(${wishlist.editors.joinToString { it.username }})",
                      style = WishlifyTheme.typography.bodyMedium,
                      color = WishlifyTheme.colorScheme.outlineVariant
                    )
                  }
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
                    text = wishlist.createdAt.formatted(),
                    style = WishlifyTheme.typography.bodyMedium,
                    color = WishlifyTheme.colorScheme.secondary
                  )
                }
              }
            }
          }
        }

        if (wishlist is Wishlist.Shared) {

          Spacer(modifier = Modifier.height(16.dp))

          WishlistInfoSharedBanner(wishlist)
        }

        WishlistInviteLink(wishlist)

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
              text = wishlist.description.takeUnless { it.isBlank() }
                ?: stringResource(R.string.wishlists_item_description_or_notes_empty),
              style = WishlifyTheme.typography.bodyMedium,
              color = WishlifyTheme.colorScheme.onSurface.let { color ->
                if (wishlist.description.isBlank()) {
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
private fun WishlistInfoSharedBanner(wishlist: Wishlist.Shared) {

  val containerColor = if (wishlist.isFinished()) {
    WishlifyTheme.colorScheme.errorContainer.copy(alpha = .6f)
  } else {
    WishlifyTheme.colorScheme.infoContainer.copy(alpha = .6f)
  }

  val contentColor = if (wishlist.isFinished()) {
    WishlifyTheme.colorScheme.onErrorContainer
  } else {
    WishlifyTheme.colorScheme.onInfoContainer
  }

  Surface(
    modifier = Modifier.fillMaxWidth(),
    color = containerColor,
    shape = WishlifyTheme.shapes.small
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
          imageVector = if (wishlist.isFinished()) {
            Icons.Outlined.EventBusy
          } else {
            Icons.Outlined.Share
          },
          contentDescription = null,
          tint = contentColor
        )

        Text(
          text = stringResource(
            if (!wishlist.isFinished()) {
              R.string.wishlist_shared
            } else {
              R.string.wishlist_shared_finished
            }
          ),
          style = WishlifyTheme.typography.titleSmall,
          color = contentColor,
          fontWeight = FontWeight.Bold
        )
      }

      Text(
        modifier = Modifier
          .fillMaxWidth()
          .padding(start = 28.dp),
        text =
          stringResource(
            if (wishlist.isFinished()) {
              R.string.wishlist_shared_finished_info_description
            } else {
              R.string.wishlist_shared_info_description
            },
            when (wishlist.event) {
              is Wishlist.SecretSantaEvent -> stringResource(R.string.secret_santa)
              is Wishlist.SharedWishlistEvent -> stringResource(R.string.wishlist_shared_event)
            },
            wishlist.deadline.formatted()
          ),
        style = WishlifyTheme.typography.bodySmall,
        color = contentColor,
      )
    }
  }
}

@Composable
private fun WishlistInviteLink(wishlist: Wishlist) {
  val link = when {
    wishlist !is Wishlist.Shared ->
      wishlist.editorInviteLink

    wishlist.event is Wishlist.SharedWishlistEvent ->
      (wishlist.event as Wishlist.SharedWishlistEvent).inviteLink

    else -> null
  }

  if (link != null) {

    val clipboard = LocalClipboard.current
    val context = LocalContext.current
    val resources = LocalResources.current
    val scope = rememberCoroutineScope()
    val linkState = rememberTextInputState(initialValue = link.asUrl())

    Spacer(modifier = Modifier.height(16.dp))

    TextInput(
      modifier = Modifier.fillMaxWidth(),
      state = linkState,
      label = when (link.origin) {
        InviteLink.Origin.WishlistEditor -> stringResource(R.string.wishlists_new_list_editors_link_input)
        InviteLink.Origin.WishlistShare -> stringResource(R.string.wishlists_share_list_link_label)
        else -> "" // Should not happen
      },
      leadingIcon = Icons.Rounded.Link,
      trailingIcon = {
        IconButton(
          onClick = {
            scope.launch {
              clipboard.copyToClipboard(
                label = when (link.origin) {
                  InviteLink.Origin.WishlistEditor -> resources.getString(R.string.wishlists_new_list_editors_link_input)
                  InviteLink.Origin.WishlistShare -> resources.getString(R.string.wishlists_share_list_link_label)
                  else -> "" // Should not happen
                },
                text = linkState.text
              )
              Toast.makeText(
                context,
                R.string.feedback_clipboard_copied,
                Toast.LENGTH_SHORT
              ).show()
            }
          }
        ) {
          Icon(
            imageVector = Icons.Rounded.ContentCopy,
            contentDescription = "Copy",
            tint = when {
              linkState.isError -> WishlifyTheme.colorScheme.error
              else -> WishlifyTheme.colorScheme.onSurface
            }.copy(alpha = .5f),
          )
        }
      },
      readOnly = true,
      singleLine = true
    )
  }
}

@Composable
private fun Category.CategoryColor.color() = when (this) {
  Category.CategoryColor.Purple -> Color(0xFF7C4DFF)
  Category.CategoryColor.Blue -> Color(0xFF448AFF)
  Category.CategoryColor.Yellow -> Color(0xFFFFC107)
  Category.CategoryColor.Green -> Color(0xFF4CAF50)
  Category.CategoryColor.Red -> Color(0xFFF44336)
  Category.CategoryColor.Pink -> Color(0xFFE91E63)
  Category.CategoryColor.Orange -> Color(0xFFFF9800)
}