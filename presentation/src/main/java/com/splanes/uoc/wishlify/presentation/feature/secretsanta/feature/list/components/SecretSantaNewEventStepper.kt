package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.creation.model.SecretSantaNewEventStep
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@Composable
fun SecretSantaNewEventStepper(
  step: SecretSantaNewEventStep,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier,
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {

    Stepper(currentStep = step)

    Text(
      text = step.title(),
      style = WishlifyTheme.typography.titleLarge,
      color = WishlifyTheme.colorScheme.onSurface,
    )

  }
}

@Composable
private fun Stepper(currentStep: SecretSantaNewEventStep) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    SecretSantaNewEventStep.entries.forEach { step ->
      Box(
        modifier = Modifier
          .weight(1f)
          .height(8.dp)
          .then(
            if (step.order <= currentStep.order) {
              Modifier.background(
                color = WishlifyTheme.colorScheme.secondary,
                shape = RoundedCornerShape(2.dp)
              )
            } else {
              Modifier.border(
                width = 2.dp,
                color = WishlifyTheme.colorScheme.secondary,
                shape = RoundedCornerShape(2.dp)
              )
            }
          )
          .padding(vertical = 4.dp)
      )
    }
  }
}

@Composable
private fun SecretSantaNewEventStep.title() = when (this) {
  SecretSantaNewEventStep.Basics -> R.string.secret_santa_new_event_basics_title
  SecretSantaNewEventStep.Participants -> R.string.secret_santa_new_event_participants_title
  SecretSantaNewEventStep.Exclusions -> R.string.secret_santa_new_event_exclusions_title
}.let { id -> stringResource(id) }