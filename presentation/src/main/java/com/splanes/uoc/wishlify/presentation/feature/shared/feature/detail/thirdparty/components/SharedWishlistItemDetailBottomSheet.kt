package com.splanes.uoc.wishlify.presentation.feature.shared.feature.detail.thirdparty.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.GroupAdd
import androidx.compose.material.icons.rounded.EuroSymbol
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlistItem
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.WishlistItem
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonShape
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonText
import com.splanes.uoc.wishlify.presentation.common.components.image.RemoteImage
import com.splanes.uoc.wishlify.presentation.common.components.input.text.TextInput
import com.splanes.uoc.wishlify.presentation.common.components.input.text.rememberTextInputState
import com.splanes.uoc.wishlify.presentation.common.utils.capitalize
import com.splanes.uoc.wishlify.presentation.common.utils.htmlString
import com.splanes.uoc.wishlify.presentation.feature.shared.components.SharedWishlistItemStateLabel
import com.splanes.uoc.wishlify.presentation.feature.shared.components.SharedWishlistItemStateSelector
import com.splanes.uoc.wishlify.presentation.feature.shared.feature.detail.thirdparty.model.SharedWishlistItemAction
import com.splanes.uoc.wishlify.presentation.feature.shared.model.SharedWishlistItemStateAction
import com.splanes.uoc.wishlify.presentation.feature.shared.utils.descriptionText
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.utils.color
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.utils.formatPrice
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.utils.icon
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.utils.name
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SharedWishlistItemDetailBottomSheet(
  visible: Boolean,
  sheetState: SheetState,
  item: SharedWishlistItem,
  itemStateActions: List<SharedWishlistItemStateAction>,
  isButtonLoading: Boolean,
  shareRequestError: String?,
  onClearShareRequestError: () -> Unit,
  onDismiss: () -> Unit,
  onAction: (SharedWishlistItemAction) -> Unit,
) {

  var actionSelected: SharedWishlistItemAction? by remember(item) { mutableStateOf(null) }
  val isButtonEnabled by remember { derivedStateOf { actionSelected != null } }

  if (visible) {
    ModalBottomSheet(
      sheetState = sheetState,
      onDismissRequest = onDismiss
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          ItemImage(
            modifier = Modifier
              .size(155.dp, 135.dp)
              .border(
                width = 1.dp,
                color = WishlifyTheme.colorScheme.outlineVariant,
                shape = WishlifyTheme.shapes.small
              ),
            url = item.linkedItem.photoUrl
          )

          Column {
            Text(
              text = item.linkedItem.name,
              style = WishlifyTheme.typography.titleLarge,
              color = WishlifyTheme.colorScheme.onSurface
            )

            HorizontalDivider(
              modifier = Modifier.fillMaxWidth(),
              color = WishlifyTheme.colorScheme.outlineVariant
            )

            Spacer(Modifier.height(8.dp))

            ItemPrice(item.linkedItem)

            Spacer(Modifier.height(4.dp))

            if (item.linkedItem.priority != WishlistItem.Priority.Standard) {
              ItemPriority(item.linkedItem.priority)
            }

            Spacer(Modifier.height(4.dp))

            ItemAmount(item.linkedItem.amount)

            if (item.linkedItem.store.isNotBlank()) {
              ItemStore(item.linkedItem.store)
            }
          }
        }

        if (item.linkedItem.link.isNotBlank()) {
          ItemLink(onClick = { onAction(SharedWishlistItemAction.OpenLink) })
        } else {
          Spacer(Modifier.height(16.dp))
        }

        ItemState(
          state = item.state,
          actions = itemStateActions,
          shareRequestError = shareRequestError,
          onClearShareRequestError = onClearShareRequestError,
          onActionSelected = { actionSelected = it }
        )

        Spacer(Modifier.height(16.dp))

        ItemDescription(item.linkedItem.description)

        Spacer(Modifier.height(20.dp))

        Button(
          modifier = Modifier.fillMaxWidth(),
          shapes = ButtonShape,
          enabled = isButtonEnabled,
          onClick = {
            if (!isButtonLoading) {
              actionSelected?.run(onAction)
            }
          }
        ) {
          if (isButtonLoading) {
            CircularProgressIndicator(
              modifier = Modifier.size(20.dp),
              color = WishlifyTheme.colorScheme.onPrimary
            )
          } else {
            ButtonText(text = stringResource(R.string.update))
          }
        }

        Spacer(Modifier.height(16.dp))
      }
    }
  }
}

@Composable
private fun ItemImage(
  modifier: Modifier,
  url: String?
) {
  Box(modifier = modifier) {
    when (url) {
      null -> {
        Image(
          modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
            .background(
              color = WishlifyTheme.colorScheme.surfaceBright,
              shape = WishlifyTheme.shapes.small
            ),
          painter = painterResource(R.drawable.item_placeholder),
          contentDescription = null,
          contentScale = ContentScale.Crop
        )
      }

      else -> {
        RemoteImage(
          modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
            .clip(WishlifyTheme.shapes.small),
          url = url,
          contentScale = ContentScale.Crop
        )
      }
    }
  }
}

@Composable
private fun ItemPrice(item: SharedWishlistItem.LinkedItem) {
  Row(verticalAlignment = Alignment.Bottom) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Text(
        text = item.price.formatPrice(includeCurrency = false),
        style = WishlifyTheme.typography.titleLarge,
        color = WishlifyTheme.colorScheme.onSurface
      )

      Spacer(Modifier.width(2.dp))

      Icon(
        modifier = Modifier.size(20.dp),
        imageVector = Icons.Rounded.EuroSymbol,
        contentDescription = item.price.formatPrice(includeCurrency = false),
        tint = WishlifyTheme.colorScheme.onSurface
      )
    }

    if (item.amount > 1) {
      Spacer(Modifier.width(6.dp))

      Text(
        modifier = Modifier.padding(bottom = 4.dp),
        text = stringResource(
          R.string.wishlists_item_price_per_unit,
          item.unitPrice.formatPrice(includeCurrency = false)
        ),
        style = WishlifyTheme.typography.labelSmall,
        color = WishlifyTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}

@Composable
private fun ItemPriority(priority: WishlistItem.Priority) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(4.dp)
  ) {
    Text(
      text = stringResource(R.string.wishlists_item_priority_label),
      style = WishlifyTheme.typography.labelSmall,
      color = WishlifyTheme.colorScheme.onSurfaceVariant
    )

    Icon(
      modifier = Modifier.size(16.dp),
      imageVector = priority.icon(),
      contentDescription = priority.name(),
      tint = priority.color()
    )

    Text(
      text = priority.name(),
      style = WishlifyTheme.typography.bodyMedium,
      color = priority.color()
    )
  }
}

@Composable
private fun ItemAmount(amount: Int) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(4.dp)
  ) {
    Text(
      text = stringResource(R.string.wishlists_item_quantity_label),
      style = WishlifyTheme.typography.labelSmall,
      color = WishlifyTheme.colorScheme.onSurfaceVariant
    )

    Text(
      text = stringResource(R.string.wishlists_item_quantity_amount, amount),
      style = WishlifyTheme.typography.bodyMedium,
      color = WishlifyTheme.colorScheme.onSurface
    )
  }
}

@Composable
private fun ItemStore(store: String) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(4.dp)
  ) {
    Text(
      text = stringResource(R.string.wishlists_item_store_label),
      style = WishlifyTheme.typography.labelSmall,
      color = WishlifyTheme.colorScheme.onSurfaceVariant
    )

    Text(
      text = store.capitalize(),
      style = WishlifyTheme.typography.bodyMedium,
      color = WishlifyTheme.colorScheme.onSurface
    )
  }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ItemLink(onClick: () -> Unit) {
  TextButton(
    shapes = ButtonShape,
    onClick = onClick,
  ) {
    Icon(
      modifier = Modifier.size(20.dp),
      imageVector = Icons.Rounded.Link,
      contentDescription = stringResource(R.string.wishlists_item_link_to_product),
    )

    Spacer(Modifier.width(4.dp))

    ButtonText(
      text = stringResource(R.string.wishlists_item_link_to_product),
      style = WishlifyTheme.typography.labelLarge
    )
  }
}

@Composable
private fun ItemState(
  state: SharedWishlistItem.State,
  actions: List<SharedWishlistItemStateAction>,
  shareRequestError: String?,
  onClearShareRequestError: () -> Unit,
  onActionSelected: (action: SharedWishlistItemAction?) -> Unit
) {

  var actionSelected: SharedWishlistItemStateAction? by remember(state) { mutableStateOf(null) }
  val numOfParticipantsState = rememberTextInputState(onClearError = onClearShareRequestError)

  LaunchedEffect(shareRequestError) {
    numOfParticipantsState.error(shareRequestError)
  }

  LaunchedEffect(actionSelected) {
    val numOfParticipants = numOfParticipantsState.text.toIntOrNull() ?: 0
    onActionSelected(actionSelected?.toItemAction(numOfParticipants))
  }

  Column {
    Row {
      Text(
        modifier = Modifier.weight(1f),
        text = stringResource(R.string.shared_wishlists_item_state_title),
        style = WishlifyTheme.typography.titleMedium,
        color = WishlifyTheme.colorScheme.onSurface
      )

      if (state !is SharedWishlistItem.Available) {
        SharedWishlistItemStateLabel(state)
      }
    }

    Spacer(Modifier.height(12.dp))

    Text(
      text = state.descriptionText(),
      style = WishlifyTheme.typography.bodySmall,
      color = WishlifyTheme.colorScheme.onSurface
    )

    Spacer(Modifier.height(12.dp))

    SharedWishlistItemStateSelector(
      modifier = Modifier.fillMaxWidth(),
      state = state,
      actions = actions,
      onActionSelected = { actionSelected = it }
    )

    AnimatedVisibility(
      modifier = Modifier.padding(top = 12.dp),
      visible = actionSelected == SharedWishlistItemStateAction.RequestShare,
      enter = expandVertically(),
      exit = shrinkVertically()
    ) {
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = WishlifyTheme.shapes.small,
        color = WishlifyTheme.colorScheme.infoContainer.copy(alpha = .6f)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

          Text(
            text = htmlString(R.string.shared_wishlists_item_state_request_share_num_of_participants_text),
            style = WishlifyTheme.typography.bodySmall,
            color = WishlifyTheme.colorScheme.onSurface
          )

          TextInput(
            modifier = Modifier.fillMaxWidth(),
            state = numOfParticipantsState,
            leadingIcon = Icons.Outlined.GroupAdd,
            label = stringResource(R.string.shared_wishlists_item_state_request_share_num_of_participants_label),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
          )
        }
      }
    }
  }
}

@Composable
private fun ItemDescription(description: String) {
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
        text = description.takeUnless { it.isBlank() }
          ?: stringResource(R.string.wishlists_item_description_or_notes_empty),
        style = WishlifyTheme.typography.bodyMedium,
        color = WishlifyTheme.colorScheme.onSurface.let { color ->
          if (description.isBlank()) {
            color.copy(alpha = .6f)
          } else {
            color
          }
        },
        textAlign = TextAlign.Justify
      )
    }
  }
}

private fun SharedWishlistItemStateAction.toItemAction(numOfParticipants: Int) =
  when (this) {
    SharedWishlistItemStateAction.Purchase -> SharedWishlistItemAction.Purchase
    SharedWishlistItemStateAction.Lock -> SharedWishlistItemAction.Lock
    SharedWishlistItemStateAction.RequestShare -> SharedWishlistItemAction.ShareRequest(numOfParticipants)
    SharedWishlistItemStateAction.Unlock -> SharedWishlistItemAction.Unlock
    SharedWishlistItemStateAction.JoinToShareRequest -> SharedWishlistItemAction.JoinToShareRequest
    SharedWishlistItemStateAction.CancelShareRequest -> SharedWishlistItemAction.CancelShareRequest
  }