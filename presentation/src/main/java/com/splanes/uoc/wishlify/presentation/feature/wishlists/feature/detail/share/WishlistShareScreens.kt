package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.share

import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.domain.feature.groups.model.Group
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.EmptyState
import com.splanes.uoc.wishlify.presentation.common.components.ErrorDialog
import com.splanes.uoc.wishlify.presentation.common.components.Loader
import com.splanes.uoc.wishlify.presentation.common.components.OrDivider
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonShape
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonText
import com.splanes.uoc.wishlify.presentation.common.components.button.IconButtonShape
import com.splanes.uoc.wishlify.presentation.common.components.image.ImagePreset
import com.splanes.uoc.wishlify.presentation.common.components.image.RemoteImage
import com.splanes.uoc.wishlify.presentation.common.components.input.date.DateInput
import com.splanes.uoc.wishlify.presentation.common.components.input.dropdown.DropdownInput
import com.splanes.uoc.wishlify.presentation.common.components.input.text.TextInput
import com.splanes.uoc.wishlify.presentation.common.components.input.text.rememberTextInputState
import com.splanes.uoc.wishlify.presentation.common.utils.copyToClipboard
import com.splanes.uoc.wishlify.presentation.common.utils.formatted
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.share.components.WishlistShareInfoBottomSheet
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.share.components.WishlistShareWarningBanner
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WishlistShareScreen(
  uiState: WishlistShareUiState.Share,
  onShare: (group: Group?, editorsCanSeeUpdates: Boolean, deadline: Long) -> Unit,
  onCreateGroup: () -> Unit,
  onClearDateError: () -> Unit,
  onDismissError: () -> Unit,
  onCancel: () -> Unit,
) {

  val context = LocalContext.current
  val resources = LocalResources.current
  val clipboard = LocalClipboard.current
  val scope = rememberCoroutineScope()

  val linkState = rememberTextInputState(
    initialValue = uiState.shareLink
  )
  val dateState = rememberTextInputState(
    onClearError = onClearDateError
  )
  var dateMillis: Long? by remember { mutableStateOf(null) }
  var groupSelected: Group.Basic? by remember { mutableStateOf(null) }
  var editorsCanSeeUpdates by remember { mutableStateOf(false) }

  val isButtonEnabled by remember {
    derivedStateOf {
      dateState.text.isNotBlank()
    }
  }

  var isVisibilityInfoModalOpen by remember { mutableStateOf(false) }
  val visibilityInfoSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  LaunchedEffect(dateMillis) {
    dateState.onValueChanged(dateMillis?.formatted().orEmpty())
  }

  LaunchedEffect(uiState.inputDateError) {
    dateState.error(uiState.inputDateError)
  }

  Box(modifier = Modifier.fillMaxSize()) {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        TopAppBar(
          title = { Text(text = stringResource(R.string.wishlist_share_title)) },
          actions = {
            IconButton(
              shapes = IconButtonShape,
              onClick = onCancel
            ) {
              Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = stringResource(R.string.cancel)
              )
            }
          }
        )
      },
    ) { paddings ->
      Column(
        modifier = Modifier
          .fillMaxSize()
          .verticalScroll(rememberScrollState())
          .padding(paddings)
          .padding(
            horizontal = 16.dp,
            vertical = 24.dp
          ),
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          when (val photo = uiState.wishlist.photo) {
            is ImageMedia.Preset -> {
              val preset = remember(photo) { ImagePreset.findById(photo.id.toInt()) }
              Image(
                modifier = Modifier
                  .size(155.dp, 135.dp)
                  .border(
                    width = 1.dp,
                    color = WishlifyTheme.colorScheme.outlineVariant,
                    shape = WishlifyTheme.shapes.small
                  )
                  .background(
                    color = WishlifyTheme.colorScheme.surfaceBright,
                    shape = WishlifyTheme.shapes.small
                  ),
                painter = painterResource(preset.res),
                contentDescription = null,
                contentScale = ContentScale.Crop
              )
            }

            is ImageMedia.Url ->
              RemoteImage(
                modifier = Modifier
                  .size(155.dp, 135.dp)
                  .border(
                    width = 1.dp,
                    color = WishlifyTheme.colorScheme.outlineVariant,
                    shape = WishlifyTheme.shapes.small
                  )
                  .clip(WishlifyTheme.shapes.small),
                url = photo.url,
                contentScale = ContentScale.Crop
              )
          }

          Column {
            Text(
              text = uiState.wishlist.title,
              style = WishlifyTheme.typography.titleLarge,
              color = WishlifyTheme.colorScheme.onSurface,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(12.dp))

            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(
                painter = painterResource(R.drawable.ic_gift),
                contentDescription = null,
                tint = WishlifyTheme.colorScheme.secondary,
              )

              Text(
                text = pluralStringResource(
                  R.plurals.wishlists_list_item_count,
                  uiState.wishlist.numOfItems,
                  uiState.wishlist.numOfItems
                ),
                style = WishlifyTheme.typography.bodyLarge,
                color = WishlifyTheme.colorScheme.secondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        DropdownInput(
          modifier = Modifier.fillMaxWidth(),
          items = buildList {
            uiState.groups.mapIndexed { index, group ->
              DropdownInput.Option(
                id = index,
                text = group.name,
              )
            }.let(::addAll)

            add(
              DropdownInput.Button(
                id = 1,
                text = stringResource(R.string.wishlists_create_group),
                leadingIcon = rememberVectorPainter(Icons.Rounded.Add)
              )
            )
          },
          label = stringResource(R.string.wishlists_share_select_group_input),
          leadingIcon = Icons.Outlined.Group,
          onSelectionChanged = { index ->
            groupSelected = index?.let { uiState.groups.getOrNull(index) }
          },
          onAdd = { onCreateGroup() }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OrDivider(
          modifier = Modifier.fillMaxWidth(),
          text = stringResource(R.string.wishlists_share_or_share_by_link)
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextInput(
          modifier = Modifier.fillMaxWidth(),
          state = linkState,
          label = stringResource(R.string.wishlists_share_list_link),
          leadingIcon = Icons.Rounded.Link,
          trailingIcon = {
            IconButton(
              onClick = {
                scope.launch {
                  clipboard.copyToClipboard(
                    label = resources.getString(R.string.wishlists_share_list_link),
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
                tint = WishlifyTheme.colorScheme.onSurface.copy(alpha = .5f),
              )
            }
          },
          readOnly = true,
          singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Surface(
            shape = WishlifyTheme.shapes.small,
            color = Color.Transparent,
            onClick = { isVisibilityInfoModalOpen = true },
          ) {
            Icon(
              modifier = Modifier.padding(4.dp),
              imageVector = Icons.Outlined.Info,
              contentDescription = null,
              tint = WishlifyTheme.colorScheme.onSurface
            )
          }

          Text(
            modifier = Modifier.weight(1f),
            text = stringResource(R.string.wishlists_share_visibility_switch_input),
            style = WishlifyTheme.typography.bodyLarge,
            color = WishlifyTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )

          Switch(
            checked = editorsCanSeeUpdates,
            onCheckedChange = { editorsCanSeeUpdates = it }
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        DateInput(
          modifier = Modifier.fillMaxWidth(),
          state = dateState,
          label = stringResource(R.string.wishlists_share_date_limit),
          valueMillis = dateMillis,
          onValueChanged = { millis -> dateMillis = millis }
        )

        Spacer(modifier = Modifier.weight(1f))

        if (editorsCanSeeUpdates) {
          WishlistShareWarningBanner(modifier = Modifier.fillMaxWidth())
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
          modifier = Modifier.fillMaxWidth(),
          enabled = isButtonEnabled,
          shapes = ButtonShape,
          onClick = {
            onShare(
              groupSelected,
              editorsCanSeeUpdates,
              dateMillis ?: 0L
            )
          }
        ) {
          ButtonText(
            text = stringResource(R.string.share)
          )
        }
      }
    }

    WishlistShareInfoBottomSheet(
      visible = isVisibilityInfoModalOpen,
      sheetState = visibilityInfoSheetState,
      onDismiss = { isVisibilityInfoModalOpen = false }
    )

    uiState.error?.let { error ->
      ErrorDialog(
        uiModel = error,
        onDismiss = onDismissError,
      )
    }

    if (uiState.isLoading) {
      Loader(modifier = Modifier.fillMaxSize())
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WishlistShareLoadingScreen(
  onCancel: () -> Unit,
) {
  Box(modifier = Modifier.fillMaxSize()) {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        TopAppBar(
          title = { Text(text = stringResource(R.string.wishlist_share_title)) },
          actions = {
            IconButton(
              shapes = IconButtonShape,
              onClick = onCancel
            ) {
              Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = stringResource(R.string.cancel)
              )
            }
          }
        )
      },
    ) { paddings ->
      Column(
        modifier = Modifier
          .fillMaxSize()
          .verticalScroll(rememberScrollState())
          .padding(paddings)
          .padding(
            horizontal = 16.dp,
            vertical = 24.dp
          ),
        verticalArrangement = Arrangement.Center
      ) {

        Loader(
          modifier = Modifier.fillMaxWidth(),
          containerColor = Color.Transparent
        )
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WishlistShareErrorScreen(
  onCancel: () -> Unit,
) {
  Box(modifier = Modifier.fillMaxSize()) {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        TopAppBar(
          title = { Text(text = stringResource(R.string.wishlist_share_title)) },
          actions = {
            IconButton(
              shapes = IconButtonShape,
              onClick = onCancel
            ) {
              Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = stringResource(R.string.cancel)
              )
            }
          }
        )
      },
    ) { paddings ->
      Column(
        modifier = Modifier
          .fillMaxSize()
          .verticalScroll(rememberScrollState())
          .padding(paddings)
          .padding(
            horizontal = 16.dp,
            vertical = 24.dp
          ),
      ) {

        Spacer(Modifier.weight(.5f))

        // Used as error component as well
        EmptyState(
          modifier = Modifier.fillMaxWidth(),
          image = painterResource(R.drawable.generic_error),
          title = stringResource(R.string.wishlists_detail_error_title),
          description = stringResource(R.string.wishlists_detail_error_description)
        )

        Spacer(Modifier.weight(1f))
      }
    }
  }
}