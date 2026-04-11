package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.edition

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Notes
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.outlined.Sell
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.PersonPinCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Category
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Wishlist
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.EmptyState
import com.splanes.uoc.wishlify.presentation.common.components.ErrorDialog
import com.splanes.uoc.wishlify.presentation.common.components.ImagePicker
import com.splanes.uoc.wishlify.presentation.common.components.Loader
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonShape
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonText
import com.splanes.uoc.wishlify.presentation.common.components.button.IconButtonShape
import com.splanes.uoc.wishlify.presentation.common.components.image.ImagePreset
import com.splanes.uoc.wishlify.presentation.common.components.input.dropdown.DropdownInput
import com.splanes.uoc.wishlify.presentation.common.components.input.text.TextInput
import com.splanes.uoc.wishlify.presentation.common.components.input.text.rememberTextInputState
import com.splanes.uoc.wishlify.presentation.common.utils.copyToClipboard
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.components.NewCategoryBottomSheet
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.creation.model.WishlistsNewListForm
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WishlistsEditListScreen(
  uiState: WishlistsEditListUiState.Form,
  onEdit: (form: WishlistsNewListForm, isOwnWishlist: Boolean) -> Unit,
  onCreateCategory: (name: String, color: Category.CategoryColor) -> Unit,
  onChangeNewCategoryModalVisibility: (visible: Boolean) -> Unit,
  onCancel: () -> Unit,
  onClearInputError: (WishlistsNewListForm.Input) -> Unit,
  onDismissError: () -> Unit,
) {

  val clipboard = LocalClipboard.current
  val context = LocalContext.current
  val resources = LocalResources.current
  val scope = rememberCoroutineScope()

  val nameState = rememberTextInputState(
    initialValue = uiState.wishlist.title,
    onClearError = { onClearInputError(WishlistsNewListForm.Input.Name) }
  )

  val targetState = rememberTextInputState(
    initialValue = (uiState.wishlist as? Wishlist.ThirdParty)?.target.orEmpty(),
    onClearError = { onClearInputError(WishlistsNewListForm.Input.Target) }
  )

  val descriptionState = rememberTextInputState(
    initialValue = uiState.wishlist.description,
    supportingText = stringResource(R.string.wishlists_new_list_description_input_support),
    onClearError = { onClearInputError(WishlistsNewListForm.Input.Description) }
  )

  val linkState = rememberTextInputState(initialValue = uiState.wishlist.editorInviteLink.asUrl())

  var categoryIndexSelected: Int? by remember {
    val selected = uiState.wishlist.category?.let {
      category -> uiState.categories.indexOfFirst { c -> c.id == category.category.id }
    }
    mutableStateOf(selected)
  }

  var isNewCategoryModalOpen by remember { mutableStateOf(false) }
  val newCategorySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  var imageSelected: ImagePicker.Resource? by remember {
    val resource = when (val photo = uiState.wishlist.photo) {
      is ImageMedia.Preset -> WishlistsPresets.find { preset -> preset.id.toString() == photo.id }
      is ImageMedia.Url -> ImagePicker.Url(photo.url)
    }
    mutableStateOf(resource)
  }

  var isOwnWishlist by remember { mutableStateOf(uiState.wishlist is Wishlist.Own) }

  val isButtonEnabled by remember {
    derivedStateOf {
      nameState.text.isNotBlank() && (isOwnWishlist || targetState.text.isNotBlank())
    }
  }

  LaunchedEffect(uiState.nameError) {
    nameState.error(uiState.nameError)
  }

  LaunchedEffect(uiState.targetError) {
    targetState.error(uiState.targetError)
  }

  LaunchedEffect(uiState.descriptionError) {
    descriptionState.error(uiState.descriptionError)
  }

  LaunchedEffect(uiState.isNewCategoryModalOpen) {
    if (uiState.isNewCategoryModalOpen) {
      isNewCategoryModalOpen = true
    } else {
      launch { newCategorySheetState.hide() }.invokeOnCompletion { isNewCategoryModalOpen = false }
    }
  }

  Box(modifier = Modifier.fillMaxSize()) {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        TopAppBar(
          title = { Text(text = stringResource(R.string.wishlists_edit_list_title)) },
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
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {

          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ImagePicker(
              modifier = Modifier
                .width(135.dp)
                .height(122.dp),
              preset = WishlistsPresets,
              onSelectionChanged = { image -> imageSelected = image }
            )

            Text(
              modifier = Modifier.padding(start = 8.dp),
              text = stringResource(R.string.optional),
              style = WishlifyTheme.typography.bodySmall,
              color = WishlifyTheme.colorScheme.onSurface.copy(alpha = .7f)
            )
          }

          Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            TextInput(
              modifier = Modifier.fillMaxWidth(),
              state = nameState,
              label = stringResource(R.string.wishlists_new_list_name_input),
              leadingIcon = painterResource(R.drawable.ic_wishlists),
              singleLine = true
            )

            DropdownInput(
              modifier = Modifier.fillMaxWidth(),
              items = buildList {
                // Categories
                uiState.categories.mapIndexed { index, category ->
                  DropdownInput.Option(
                    id = index,
                    text = category.name,
                    trailingIcon = rememberVectorPainter(Icons.Filled.Circle),
                    trailingIconColor = category.color
                  )
                }.let(::addAll)
                // Add button
                add(
                  DropdownInput.Button(
                    id = 1,
                    text = stringResource(R.string.wishlists_new_category),
                    leadingIcon = rememberVectorPainter(Icons.Rounded.Add)
                  )
                )
              },
              label = stringResource(R.string.wishlists_category),
              showButtonSpacer = uiState.categories.isNotEmpty(),
              leadingIcon = Icons.Outlined.Sell,
              supportingText = stringResource(R.string.optional),
              onSelectionChanged = { index -> categoryIndexSelected = index },
              onAdd = { onChangeNewCategoryModalVisibility(true) }
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = stringResource(R.string.wishlists_new_list_is_own_list_input),
            style = WishlifyTheme.typography.bodyLarge,
            color = WishlifyTheme.colorScheme.onSurface
          )

          Switch(
            checked = isOwnWishlist,
            onCheckedChange = { checked -> isOwnWishlist = checked }
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        AnimatedVisibility(
          visible = !isOwnWishlist,
          enter = expandVertically(expandFrom = Alignment.Bottom),
          exit = shrinkVertically(shrinkTowards = Alignment.Top)
        ) {
          Column {
            TextInput(
              modifier = Modifier.fillMaxWidth(),
              state = targetState,
              label = stringResource(R.string.wishlists_new_list_third_party_input),
              leadingIcon = Icons.Rounded.PersonPinCircle,
              singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))
          }
        }

        TextInput(
          modifier = Modifier.fillMaxWidth(),
          state = descriptionState,
          label = stringResource(R.string.wishlists_new_list_description_input),
          leadingIcon = Icons.AutoMirrored.Rounded.Notes,
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
          modifier = Modifier.fillMaxWidth(),
          text = stringResource(R.string.wishlists_new_list_editors_description),
          style = WishlifyTheme.typography.bodyLarge,
          color = WishlifyTheme.colorScheme.onSurface,
          textAlign = TextAlign.Justify
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextInput(
          modifier = Modifier.fillMaxWidth(),
          state = linkState,
          label = stringResource(R.string.wishlists_new_list_editors_link_input),
          leadingIcon = Icons.Rounded.Link,
          trailingIcon = {
            IconButton(
              onClick = {
                scope.launch {
                  clipboard.copyToClipboard(
                    label = resources.getString(R.string.wishlists_new_list_editors_link_input),
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

        Spacer(modifier = Modifier.weight(1f))

        Button(
          modifier = Modifier.fillMaxWidth(),
          shapes = ButtonShape,
          enabled = isButtonEnabled,
          onClick = {
            val form = WishlistsNewListForm(
              image = imageSelected,
              name = nameState.text,
              categoryIndex = categoryIndexSelected,
              target = targetState.text.takeIf { !isOwnWishlist },
              description = descriptionState.text.takeIf { it.isNotBlank() }
            )
            onEdit(form, isOwnWishlist)
          }
        ) {
          ButtonText(text = stringResource(R.string.save))
        }
      }
    }

    NewCategoryBottomSheet(
      isVisible = isNewCategoryModalOpen,
      sheetState = newCategorySheetState,
      error = uiState.newCategoryNameError,
      onClearInputError = { onClearInputError(WishlistsNewListForm.Input.NewCategoryName) },
      onDismiss = { onChangeNewCategoryModalVisibility(false) },
      onCreate = onCreateCategory
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
fun WishlistsEditListLoadingScreen(
  onCancel: () -> Unit,
) {
  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      TopAppBar(
        title = { Text(text = stringResource(R.string.wishlists_edit_list_title)) },
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

      Loader(
        modifier = Modifier.fillMaxWidth(),
        containerColor = Color.Transparent
      )

      Spacer(Modifier.weight(1f))
    }
  }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun WishlistsEditListErrorScreen(
  onCancel: () -> Unit
) {
  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      TopAppBar(
        title = { Text(text = stringResource(R.string.wishlists_edit_list_title)) },
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

private val WishlistsPresets = ImagePreset.entries.map { preset ->
  ImagePicker.Preset(
    id = preset.id,
    drawable = preset.res
  )
}