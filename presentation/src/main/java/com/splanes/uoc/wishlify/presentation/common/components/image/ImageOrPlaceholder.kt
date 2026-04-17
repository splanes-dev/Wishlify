package com.splanes.uoc.wishlify.presentation.common.components.image

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@Composable
fun ImageOrPlaceholder(
  url: String?,
  placeholder: Painter,
  modifier: Modifier = Modifier,
  shape: Shape? = null,
  contenScale: ContentScale = ContentScale.Crop,
  contentDescription: String? = null,
) {
  when (url) {
    null -> {
      Image(
        modifier = modifier
          .then(
            if (shape != null) {
              Modifier.background(
                color = WishlifyTheme.colorScheme.surfaceBright,
                shape = shape,
              )
            } else {
              Modifier
            }
          ),
        painter = placeholder,
        contentDescription = contentDescription,
        contentScale = contenScale
      )
    }
    else -> {
      RemoteImage(
        modifier = modifier
          .then(
            if (shape != null) {
              Modifier.clip(shape)
            } else {
              Modifier
            }
          ),
        url = url,
        contentScale = contenScale,
        contentDescription = contentDescription,
      )
    }
  }
}