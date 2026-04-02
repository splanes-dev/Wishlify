package com.splanes.uoc.wishlify.presentation.common.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonShape
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonText
import com.splanes.uoc.wishlify.presentation.common.components.image.RemoteImage
import com.splanes.uoc.wishlify.presentation.common.components.input.text.TextInput
import com.splanes.uoc.wishlify.presentation.common.components.input.text.rememberTextInputState
import com.splanes.uoc.wishlify.presentation.common.utils.dashedBorder
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ImagePicker(
  modifier: Modifier = Modifier,
  initial: ImagePicker.Resource? = null,
  preset: List<ImagePicker.Preset>,
  isUrlAllowed: Boolean = true,
  onSelectionChanged: (ImagePicker.Resource?) -> Unit,
) {

  var selected by remember { mutableStateOf(initial) }
  var optionSelected: SourceOption? by remember { mutableStateOf(null) }
  var isOptionsModalOpen by remember { mutableStateOf(false) }
  var isPresetModalOpen by remember { mutableStateOf(false) }
  val optionsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val presetSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val deviceImageRequest = remember {
    PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly)
  }
  val deviceLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia(),
    onResult = { uri ->
      if (uri != null) {
        selected = ImagePicker.Device(uri)
      }
    }
  )

  LaunchedEffect(optionSelected) {
    optionSelected?.let { option ->
      when (option) {
        SourceOption.Device -> {
          deviceLauncher.launch(deviceImageRequest)
        }

        SourceOption.Preset -> {
          isPresetModalOpen = true
        }

        is SourceOption.Url -> {
          selected = ImagePicker.Url(option.url)
        }

        SourceOption.DeleteSelection -> {
          selected = null
        }
      }

      optionSelected = null
    }
  }

  LaunchedEffect(selected) {
    if (selected != initial) onSelectionChanged(selected)
  }

  Surface(
    modifier = modifier
      .then(
        if (selected == null) {
          Modifier.dashedBorder(
            color = WishlifyTheme.colorScheme.outline
          )
        } else {
          Modifier
        }
      ),
    onClick = {
      if (preset.isEmpty() && !isUrlAllowed) {
        deviceLauncher.launch(deviceImageRequest)
      } else {
        isOptionsModalOpen = true
      }
    },
    border = BorderStroke(
      width = if (selected != null) 1.dp else 0.dp,
      color = WishlifyTheme.colorScheme.outline,
    ),
    shape = WishlifyTheme.shapes.small
  ) {
    Crossfade(selected) { imageSelected ->
      if (imageSelected == null) {
        ImagePickerContentEmpty()
      } else {
        ImagePickerContent(imageSelected)
      }
    }
  }

  if (isPresetModalOpen) {
    PresetBottomSheet(
      sheetState = presetSheetState,
      preset = preset,
      onDismiss = { isPresetModalOpen = false },
      onSelect = { item ->
        selected = item
        isPresetModalOpen = false
      }
    )
  }

  if (isOptionsModalOpen) {
    OptionsBottomSheet(
      sheetState = optionsSheetState,
      isUrlAllowed = isUrlAllowed,
      isPresetAllowed = preset.isNotEmpty(),
      existsSelected = selected != null,
      onDismiss = { isOptionsModalOpen = false },
      onOptionSelected = { option ->
        optionSelected = option
        isOptionsModalOpen = false
      }
    )
  }
}

@Composable
private fun ImagePickerContentEmpty() {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        color = WishlifyTheme.colorScheme.surfaceContainerLow,
        shape = WishlifyTheme.shapes.small
      ),
    contentAlignment = Alignment.Center,
  ) {
    Text(
      text = stringResource(R.string.pick_image),
      style = WishlifyTheme.typography.labelLarge,
      color = WishlifyTheme.colorScheme.onSurfaceVariant,
      textAlign = TextAlign.Center
    )
  }
}

@Composable
private fun ImagePickerContent(imageSelected: ImagePicker.Resource) {
  when (imageSelected) {
    is ImagePicker.Device -> {
      RemoteImage(
        modifier = Modifier.fillMaxSize(),
        url = imageSelected.uri.toString(),
        contentScale = ContentScale.Crop
      )
    }

    is ImagePicker.Url -> {
      RemoteImage(
        modifier = Modifier.fillMaxSize(),
        url = imageSelected.url,
        contentScale = ContentScale.Crop
      )
    }

    is ImagePicker.Preset -> {
      Image(
        modifier = Modifier
          .fillMaxSize()
          .background(color = WishlifyTheme.colorScheme.surfaceBright),
        painter = painterResource(imageSelected.drawable),
        contentDescription = null,
        contentScale = ContentScale.Crop
      )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun OptionsBottomSheet(
  sheetState: SheetState,
  onDismiss: () -> Unit,
  isPresetAllowed: Boolean,
  isUrlAllowed: Boolean,
  existsSelected: Boolean,
  onOptionSelected: (SourceOption) -> Unit,
) {
  val urlInputState = rememberTextInputState()
  val isUrlButtonEnabled by remember {
    derivedStateOf { urlInputState.text.isNotBlank() }
  }

  ModalBottomSheet(
    sheetState = sheetState,
    onDismissRequest = onDismiss
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp),
    ) {
      Text(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(R.string.pick_image_source),
        style = WishlifyTheme.typography.titleLarge,
        color = WishlifyTheme.colorScheme.onSurface
      )

      Spacer(modifier = Modifier.height(16.dp))

      TextButton(
        modifier = Modifier.fillMaxWidth(),
        shapes = ButtonShape,
        onClick = { onOptionSelected(SourceOption.Device) }
      ) {
        Icon(
          imageVector = Icons.Rounded.PhoneAndroid,
          contentDescription = stringResource(R.string.pick_image_source_device)
        )

        Spacer(modifier = Modifier.width(8.dp))

        ButtonText(
          modifier = Modifier.fillMaxWidth(),
          text = stringResource(R.string.pick_image_source_device)
        )
      }

      if (isPresetAllowed) {

        HorizontalDivider(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
          color = WishlifyTheme.colorScheme.outline.copy(alpha = .3f)
        )

        TextButton(
          modifier = Modifier.fillMaxWidth(),
          shapes = ButtonShape,
          onClick = { onOptionSelected(SourceOption.Preset) }
        ) {
          Icon(
            imageVector = Icons.Rounded.Image,
            contentDescription = stringResource(R.string.pick_image_source_preset)
          )

          Spacer(modifier = Modifier.width(8.dp))

          ButtonText(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.pick_image_source_preset)
          )
        }
      }

      if (isUrlAllowed) {

        OrDivider(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
          text = stringResource(R.string.or)
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          TextInput(
            modifier = Modifier.weight(1f),
            state = urlInputState,
            leadingIcon = Icons.Rounded.Link,
            label = stringResource(R.string.pick_image_source_link),
            singleLine = true,
            maxLines = 1,
          )

          Button(
            shapes = ButtonShape,
            enabled = isUrlButtonEnabled,
            onClick = {
              onOptionSelected(SourceOption.Url(urlInputState.text))
              urlInputState.onClear()
            },
          ) {
            ButtonText(text = stringResource(R.string.pick_image_source_load_link))
          }
        }
      }

      if (existsSelected) {
        Spacer(modifier = Modifier.height(16.dp))

        Button(
          shapes = ButtonShape,
          colors = ButtonDefaults.buttonColors(
            containerColor = WishlifyTheme.colorScheme.errorContainer,
            contentColor = WishlifyTheme.colorScheme.onErrorContainer
          ),
          onClick = { onOptionSelected(SourceOption.DeleteSelection) }
        ) {
          Icon(
            imageVector = Icons.Rounded.DeleteOutline,
            contentDescription = stringResource(R.string.pick_image_delete)
          )

          Spacer(modifier = Modifier.width(8.dp))

          ButtonText(text = stringResource(R.string.pick_image_delete))
        }
      }
    }

    Spacer(modifier = Modifier.height(24.dp))
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PresetBottomSheet(
  sheetState: SheetState,
  preset: List<ImagePicker.Preset>,
  onDismiss: () -> Unit,
  onSelect: (ImagePicker.Preset) -> Unit
) {
  ModalBottomSheet(
    sheetState = sheetState,
    onDismissRequest = onDismiss
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp),
    ) {
      Text(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(R.string.pick_image_preset_title),
        style = WishlifyTheme.typography.titleLarge,
        color = WishlifyTheme.colorScheme.onSurface
      )

      Spacer(modifier = Modifier.height(16.dp))

      LazyVerticalGrid(
        modifier = Modifier.fillMaxWidth(),
        columns = GridCells.Fixed(4),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
      ) {
        items(preset) { item ->
          Image(
            modifier = Modifier
              .aspectRatio(1f)
              .clip(WishlifyTheme.shapes.small)
              .border(
                width = 1.dp,
                color = WishlifyTheme.colorScheme.outline.copy(
                  alpha = .3f
                ),
                shape = WishlifyTheme.shapes.small
              )
              .background(
                color = WishlifyTheme.colorScheme.surfaceBright
              )
              .clickable(onClick = { onSelect(item) }),
            painter = painterResource(item.drawable),
            contentDescription = null,
            contentScale = ContentScale.Crop
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

private sealed interface SourceOption {
  data object Device : SourceOption
  data object Preset : SourceOption
  data class Url(val url: String) : SourceOption
  data object DeleteSelection : SourceOption
}

object ImagePicker {

  sealed class Resource
  data class Device(val uri: Uri) : Resource()
  data class Preset(
    val id: Int,
    val drawable: Int
  ) : Resource()

  data class Url(val url: String) : Resource()
}