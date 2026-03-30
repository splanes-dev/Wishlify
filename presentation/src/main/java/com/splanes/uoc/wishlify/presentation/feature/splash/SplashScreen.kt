package com.splanes.uoc.wishlify.presentation.feature.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@Composable
fun SplashScreen() {

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        brush = Brush.verticalGradient(
          colors = listOf(
            WishlifyTheme.colorScheme.background,
            WishlifyTheme.colorScheme.secondaryContainer
          ),
        ),
      )
      .padding(vertical = 80.dp),
  ) {
    Image(
      modifier = Modifier
        .fillMaxWidth()
        .align(Alignment.Center),
      painter = painterResource(R.drawable.app_logo),
      contentDescription = stringResource(R.string.app_name)
    )

    Text(
      modifier = Modifier.align(Alignment.BottomCenter),
      text = stringResource(R.string.app_name),
      style = WishlifyTheme.typography.decorationLarge,
      color = WishlifyTheme.colorScheme.onPrimaryContainer
    )
  }

}

@Composable
@PreviewLightDark
private fun SplashScreenPreview() {
  WishlifyTheme {
    SplashScreen()
  }
}