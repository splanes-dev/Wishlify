package com.splanes.uoc.wishlify.presentation.common.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.image.ImagePreset
import com.splanes.uoc.wishlify.presentation.common.components.image.RemoteImage
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@Composable
fun CardImage(
  media: ImageMedia?,
  modifier: Modifier = Modifier,
  width: Dp = 135.dp,
  placeholder: Painter = painterResource(R.drawable.preset_gift),
  enabled: Boolean = true,
) {

  val disabledColorFilter = remember {
    ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })
  }

  val image: @Composable (painter: Painter, contentDescription: String?) -> Unit =
    @Composable { painter, contentDescription ->
      Image(
        modifier = modifier
          .width(width)
          .fillMaxHeight()
          .background(color = WishlifyTheme.colorScheme.surfaceBright),
        painter = painter,
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        colorFilter = disabledColorFilter.takeIf { !enabled }
      )
    }

  when (media) {
    is ImageMedia.Preset -> {
      val preset = remember(media) { ImagePreset.findById(media.id.toInt()) }
      image(painterResource(preset.res), preset.name)
    }

    is ImageMedia.Url -> {
      RemoteImage(
        modifier = modifier
          .width(width)
          .fillMaxHeight(),
        url = media.url,
        contentScale = ContentScale.Crop,
        colorFilter = disabledColorFilter.takeIf { !enabled }
      )
    }

    null -> {
      image(placeholder, null)
    }
  }
}