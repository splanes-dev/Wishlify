package com.splanes.uoc.wishlify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

class WishlifyActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    installSplashScreen()

    super.onCreate(savedInstanceState)
    setContent {
      WishlifyTheme {
        Column(
          modifier = Modifier
            .fillMaxSize()
            .background(WishlifyTheme.colorScheme.background)
            .systemBarsPadding(),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center,
        ) {
          Text(
            text = "PAC1",
            style = WishlifyTheme.typography.displayMedium,
            color = WishlifyTheme.colorScheme.primary,
          )

          Text(
            text = "Hello World",
            style = WishlifyTheme.typography.bodyMedium,
            color = WishlifyTheme.colorScheme.onBackground,
          )
        }
      }
    }
  }
}