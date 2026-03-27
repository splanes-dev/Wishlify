package com.splanes.uoc.wishlify.presentation.infrastructure.theme.typography.styles

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.typography.AppTypography

private val baseline = Typography()

private val decorationFontFamily: FontFamily = FontFamily(
  Font(resId = R.font.mochiy_pop_one_regular)
)
private val regularFontFamily: FontFamily = FontFamily(
  Font(resId = R.font.product_sans_regular),
  Font(resId = R.font.product_sans_bold, weight = FontWeight.Bold),
  Font(resId = R.font.product_sans_italic, style = FontStyle.Italic),
)

internal val AppTypography = AppTypography(
  decorationLarge = baseline.displayLarge.copy(fontFamily = decorationFontFamily),
  decorationMedium = baseline.displayMedium.copy(fontFamily = decorationFontFamily),
  decorationSmall = baseline.displaySmall.copy(fontFamily = decorationFontFamily),
  displayLarge = baseline.displayLarge.copy(fontFamily = regularFontFamily),
  displayMedium = baseline.displayMedium.copy(fontFamily = regularFontFamily),
  displaySmall = baseline.displaySmall.copy(fontFamily = regularFontFamily),
  headlineLarge = baseline.headlineLarge.copy(fontFamily = regularFontFamily),
  headlineMedium = baseline.headlineMedium.copy(fontFamily = regularFontFamily),
  headlineSmall = baseline.headlineSmall.copy(fontFamily = regularFontFamily),
  titleLarge = baseline.titleLarge.copy(fontFamily = regularFontFamily),
  titleMedium = baseline.titleMedium.copy(fontFamily = regularFontFamily),
  titleSmall = baseline.titleSmall.copy(fontFamily = regularFontFamily),
  bodyLarge = baseline.bodyLarge.copy(fontFamily = regularFontFamily),
  bodyMedium = baseline.bodyMedium.copy(fontFamily = regularFontFamily),
  bodySmall = baseline.bodySmall.copy(fontFamily = regularFontFamily),
  labelLarge = baseline.labelLarge.copy(fontFamily = regularFontFamily),
  labelMedium = baseline.labelMedium.copy(fontFamily = regularFontFamily),
  labelSmall = baseline.labelSmall.copy(fontFamily = regularFontFamily),
)