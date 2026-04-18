package com.splanes.uoc.wishlify.presentation.common.components.button

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonShapes
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonShapes
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButtonShapes
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
val ButtonShape: ButtonShapes
  @Composable
  get() = ButtonShapes(
    shape = WishlifyTheme.shapes.small,
    pressedShape = WishlifyTheme.shapes.extraSmall
  )

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
val IconButtonShape: IconButtonShapes
  @Composable
  get() = IconButtonShapes(
    shape = WishlifyTheme.shapes.medium,
    pressedShape = WishlifyTheme.shapes.small
  )

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
val ToggleButtonShape: ToggleButtonShapes
  @Composable
  get() = ToggleButtonShapes(
    shape = WishlifyTheme.shapes.medium,
    pressedShape = WishlifyTheme.shapes.largeIncreased,
    checkedShape = WishlifyTheme.shapes.large
  )

@Composable
fun ButtonText(
  text: String,
  modifier: Modifier = Modifier,
  style: TextStyle = WishlifyTheme.typography.titleMedium
) {
  Text(
    modifier = modifier,
    text = text,
    style = style,
  )
}

@Composable
fun IconButtonCustom(
  imageVector: ImageVector,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  containerColor: Color = Color.Transparent,
  contentColor: Color = contentColorFor(containerColor),
  contentSize: DpSize = DpSize(16.dp, 16.dp),
  contentPadding: PaddingValues = PaddingValues(all = 4.dp),
  contentDescription: String? = null,
  onClick: () -> Unit,
) {
  IconButtonCustom(
    modifier = modifier,
    painter = rememberVectorPainter(imageVector),
    enabled = enabled,
    containerColor = containerColor,
    contentColor = contentColor,
    contentSize = contentSize,
    contentPadding = contentPadding,
    contentDescription = contentDescription,
    onClick = onClick,
  )
}

@Composable
fun IconButtonCustom(
  painter: Painter,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  shape: Shape = WishlifyTheme.shapes.extraSmall,
  containerColor: Color = Color.Transparent,
  contentColor: Color = contentColorFor(containerColor),
  contentSize: DpSize = DpSize(24.dp, 24.dp),
  contentPadding: PaddingValues = PaddingValues(all = 4.dp),
  contentDescription: String? = null,
  onClick: () -> Unit,
) {

  val interactionSource = remember { MutableInteractionSource() }

  Box(
    modifier = modifier
      .size(contentSize)
      .clip(shape)
      .background(containerColor)
      .clickable(
        enabled = enabled,
        interactionSource = interactionSource,
        indication = ripple(),
        onClick = onClick
      ),
    contentAlignment = Alignment.Center
  ) {
    Icon(
      modifier = Modifier.padding(contentPadding),
      painter = painter,
      contentDescription = contentDescription,
      tint = contentColor,
    )
  }
}