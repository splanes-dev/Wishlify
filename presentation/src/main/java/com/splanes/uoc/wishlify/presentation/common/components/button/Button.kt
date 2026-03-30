package com.splanes.uoc.wishlify.presentation.common.components.button

import androidx.compose.material3.ButtonShapes
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.IconButtonShapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable
fun ButtonText(text: String) {
  Text(
    text = text,
    style = WishlifyTheme.typography.titleMedium,
  )
}