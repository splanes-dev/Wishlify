package com.splanes.uoc.wishlify.presentation.infrastructure.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.colors.AppColorScheme
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.colors.palette.DarkColorScheme
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.colors.palette.LightColorScheme
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.colors.toMaterial3
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.typography.AppTypography
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.typography.styles.AppTypography
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.typography.toMaterial3

internal val LocalAppColorScheme = staticCompositionLocalOf<AppColorScheme> {
  error("No AppColorScheme provided")
}

internal val LocalAppTypography = staticCompositionLocalOf { AppTypography }

object WishlifyTheme {
  val colorScheme: AppColorScheme
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColorScheme.current

  val typography: AppTypography
    @Composable
    @ReadOnlyComposable
    get() = LocalAppTypography.current

  val shapes: Shapes
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.shapes
}

@Composable
fun WishlifyTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit
) {
  val colorScheme = when {
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as Activity).window
      window.statusBarColor = colorScheme.background.toArgb()
      WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
    }
  }

  CompositionLocalProvider(LocalAppColorScheme provides colorScheme) {
    MaterialTheme(
      colorScheme = colorScheme.toMaterial3(),
      typography = LocalAppTypography.current.toMaterial3(),
      content = content
    )
  }
}