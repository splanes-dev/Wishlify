package com.splanes.uoc.wishlify.presentation.feature.shared.feature.detail.thirdparty.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.GroupAdd
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlistItem
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonShape
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonText
import com.splanes.uoc.wishlify.presentation.common.components.input.text.TextInput
import com.splanes.uoc.wishlify.presentation.common.components.input.text.rememberTextInputState
import com.splanes.uoc.wishlify.presentation.common.utils.htmlString
import com.splanes.uoc.wishlify.presentation.feature.shared.components.SharedWishlistItemStateSelector
import com.splanes.uoc.wishlify.presentation.feature.shared.feature.detail.thirdparty.model.SharedWishlistItemAction
import com.splanes.uoc.wishlify.presentation.feature.shared.model.SharedWishlistItemStateAction
import com.splanes.uoc.wishlify.presentation.feature.shared.utils.descriptionText
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme
import timber.log.Timber


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharedWishlistItemStateBottomSheet(
  visible: Boolean,
  sheetState: SheetState,
  item: SharedWishlistItem,
  shareRequestError: String?,
  onUpdateState: (SharedWishlistItemAction.UpdateState) -> Unit,
  onDismiss: () -> Unit,
  onClearShareRequestError: () -> Unit,
) {

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

        when (val state = item.state) {
          SharedWishlistItem.Available ->
            SharedWishlistItemStateAvailableContent(
              shareRequestError = shareRequestError,
              onClearShareRequestError = onClearShareRequestError,
              onActionSelected = { action -> action?.run(onUpdateState) },
            )

          is SharedWishlistItem.ShareRequest ->
            SharedWishlistItemStateShareRequestContent(
              state = state,
              onJoinToShare = { onUpdateState(SharedWishlistItemAction.JoinToShareRequest) }
            )

          else -> {
            // Others states should not be possible
            Timber.e("Trying to show update state bottom sheet with state=`${item.state}`")
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SharedWishlistItemStateAvailableContent(
  shareRequestError: String?,
  onClearShareRequestError: () -> Unit,
  onActionSelected: (SharedWishlistItemAction.UpdateState?) -> Unit,
) {

  var selected: SharedWishlistItemStateAction? by remember { mutableStateOf(null) }
  val numOfParticipantsState = rememberTextInputState(onClearError = onClearShareRequestError)

  LaunchedEffect(shareRequestError) {
    numOfParticipantsState.error(shareRequestError)
  }

  val isButtonEnabled by remember {
    derivedStateOf {
      selected != null && (selected != SharedWishlistItemStateAction.RequestShare || numOfParticipantsState.text.isNotBlank())
    }
  }

  Text(
    modifier = Modifier.fillMaxWidth(),
    text = stringResource(R.string.shared_wishlists_update_state_bottom_sheet_title),
    style = WishlifyTheme.typography.titleLarge,
    color = WishlifyTheme.colorScheme.onSurface
  )

  Spacer(modifier = Modifier.height(16.dp))

  SharedWishlistItemStateSelector(
    modifier = Modifier.fillMaxWidth(),
    state = SharedWishlistItem.Available,
    actions = listOf(
      SharedWishlistItemStateAction.Purchase,
      SharedWishlistItemStateAction.Lock,
      SharedWishlistItemStateAction.RequestShare,
    ),
    onActionSelected = { action -> selected = action }
  )

  AnimatedVisibility(
    modifier = Modifier.padding(top = 12.dp),
    visible = selected == SharedWishlistItemStateAction.RequestShare,
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

  Spacer(modifier = Modifier.height(16.dp))

  Button(
    modifier = Modifier.fillMaxWidth(),
    shapes = ButtonShape,
    enabled = isButtonEnabled,
    onClick = {
      selected
        ?.let { action ->
          when (action) {
            SharedWishlistItemStateAction.Purchase -> SharedWishlistItemAction.Purchase
            SharedWishlistItemStateAction.Lock -> SharedWishlistItemAction.Lock
            SharedWishlistItemStateAction.RequestShare ->
              SharedWishlistItemAction.ShareRequest(numOfParticipantsState.text.toIntOrNull() ?: 0)
            else -> null
          }
        }
        ?.let { action -> onActionSelected(action) }
    }
  ) {
    ButtonText(text = stringResource(R.string.update))
  }

  Spacer(Modifier.height(16.dp))
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SharedWishlistItemStateShareRequestContent(
  state: SharedWishlistItem.ShareRequest,
  onJoinToShare: () -> Unit
) {
  Text(
    modifier = Modifier.fillMaxWidth(),
    text = stringResource(R.string.shared_wishlists_join_to_share_bottom_sheet_title),
    style = WishlifyTheme.typography.titleLarge,
    color = WishlifyTheme.colorScheme.onSurface
  )

  Spacer(modifier = Modifier.height(16.dp))

  Text(
    text = state.descriptionText(),
    style = WishlifyTheme.typography.bodySmall,
    color = WishlifyTheme.colorScheme.onSurface
  )

  Spacer(modifier = Modifier.height(16.dp))

  Button(
    modifier = Modifier.fillMaxWidth(),
    shapes = ButtonShape,
    onClick = onJoinToShare
  ) {
    ButtonText(text = stringResource(R.string.shared_wishlists_join_to_share_bottom_sheet_button))
  }

  Spacer(Modifier.height(16.dp))
}