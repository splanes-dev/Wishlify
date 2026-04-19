package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.hobbies.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonShape
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonText
import com.splanes.uoc.wishlify.presentation.common.utils.htmlString
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SecretSantaAISuggestionsSection(
  modifier: Modifier = Modifier,
  onGenerateSuggestions: () -> Unit,
) {
  Column(
    modifier = modifier,
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Text(
      text = stringResource(R.string.secret_santa_hobbies_section_ia_title),
      style = WishlifyTheme.typography.titleLarge,
      color = WishlifyTheme.colorScheme.onSurface
    )

    Text(
      text = htmlString(R.string.secret_santa_hobbies_section_ia_description),
      style = WishlifyTheme.typography.bodyMedium,
      color = WishlifyTheme.colorScheme.onSurface,
      textAlign = TextAlign.Justify,
    )

    AISuggestionsBanner(modifier = Modifier.fillMaxWidth())

    Button(
      modifier = Modifier.fillMaxWidth(),
      shapes = ButtonShape,
      onClick = onGenerateSuggestions
    ) {

      Icon(
        painter = painterResource(R.drawable.ic_ia_generator),
        contentDescription = null,
      )

      ButtonText(
        modifier = Modifier.padding(horizontal = 8.dp),
        text = stringResource(R.string.secret_santa_hobbies_section_ia_button)
      )
    }
  }
}

@Composable
private fun AISuggestionsBanner(modifier: Modifier = Modifier) {
  Surface(
    modifier = modifier,
    shape = WishlifyTheme.shapes.small,
    color = WishlifyTheme.colorScheme.infoContainer
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp),
      verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Outlined.Info,
          contentDescription = null,
          tint = WishlifyTheme.colorScheme.onInfoContainer
        )

        Text(
          modifier = Modifier
            .weight(1f)
            .padding(horizontal = 8.dp),
          text = stringResource(R.string.secret_santa_hobbies_section_ia_banner_title),
          style = WishlifyTheme.typography.bodyMedium,
          color = WishlifyTheme.colorScheme.onInfoContainer,
          fontWeight = FontWeight.Bold
        )
      }

      Text(
        modifier = Modifier.padding(
          start = 32.dp,
          end = 16.dp
        ),
        text = htmlString(R.string.secret_santa_hobbies_section_ia_banner_description),
        style = WishlifyTheme.typography.bodyMedium,
        color = WishlifyTheme.colorScheme.onInfoContainer,
        textAlign = TextAlign.Justify
      )
    }
  }
}