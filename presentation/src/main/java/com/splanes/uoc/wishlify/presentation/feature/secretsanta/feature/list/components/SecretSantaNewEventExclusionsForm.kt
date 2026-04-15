package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.domain.feature.user.model.User
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonShape
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonText
import com.splanes.uoc.wishlify.presentation.common.components.button.IconButtonCustom
import com.splanes.uoc.wishlify.presentation.common.components.input.dropdown.DropdownInput
import com.splanes.uoc.wishlify.presentation.common.utils.htmlString
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.creation.model.SecretSantaNewEventForm
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SecretSantaNewEventExclusionsForm(
  form: SecretSantaNewEventForm,
  participants: List<User.Basic>,
  modifier: Modifier = Modifier,
  createButtonText: String = stringResource(R.string.create),
  onCreate: (SecretSantaNewEventForm) -> Unit,
) {

  val maxRows = remember(participants) { participants.count() * (participants.count() - 1) }

  val exclusions = remember(form.exclusions) {
    val exclusions = if (form.exclusions.count() == 0) {
      listOf(null to null)
    } else {
      form.exclusions
    }
    mutableStateListOf(*exclusions.toTypedArray())
  }

  Column(modifier = modifier) {
    LazyColumn(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      itemsIndexed(exclusions) { index, (member1, member2) ->
        ExclusionRow(
          member1 = member1,
          member2 = member2,
          exclusions = exclusions.filterNotNull(),
          participants = participants,
          onChange = { exclusion -> exclusions[index] = exclusion },
          onRemove = { exclusions.removeAt(index) }
        )
      }
      if (exclusions.count() < maxRows) {
        item {
          OutlinedButton(
            shapes = ButtonShape,
            border = BorderStroke(width = 1.dp, WishlifyTheme.colorScheme.success),
            colors = ButtonDefaults.outlinedButtonColors(
              contentColor = WishlifyTheme.colorScheme.success
            ),
            onClick = { exclusions.add(null to null) },
          ) {
            Icon(
              imageVector = Icons.Rounded.Add,
              contentDescription = null
            )

            Spacer(Modifier.width(8.dp))

            ButtonText(text = stringResource(R.string.secret_santa_select_exclusions_add_btn))
          }
        }
      }
    }

    Spacer(Modifier.height(16.dp))

    InfoBanner()

    Spacer(Modifier.weight(1f))

    Button(
      modifier = Modifier.fillMaxWidth(),
      shapes = ButtonShape,
      onClick = {
        val updated = form.copy(exclusions = exclusions.filterNotNull())
        onCreate(updated)
      }
    ) {
      ButtonText(text = createButtonText)
    }
  }
}

@Composable
private fun ExclusionRow(
  member1: User.Basic?,
  member2: User.Basic?,
  participants: List<User.Basic>,
  exclusions: List<Pair<User.Basic, User.Basic>>,
  onChange: (Pair<User.Basic?, User.Basic?>) -> Unit,
  onRemove: () -> Unit,
) {

  val options1 = participants
    .filter { participant ->
      participant.uid != member2?.uid && (participant to member2) !in exclusions
    }

  val options2 = participants
    .filter { participant ->
      participant.uid != member1?.uid && (member1 to participant) !in exclusions
    }

  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {

    DropdownInput(
      modifier = Modifier.weight(1f),
      items = options1.toOptions(),
      initial = options1.filter { it.uid == member1?.uid }.toOptions().singleOrNull(),
      label = stringResource(R.string.secret_santa_select_exclusions_input_label1),
      onSelectionChanged = { index ->
        val m1 = index?.let { options1.getOrNull(index) }
        onChange(m1 to member2)
      },
    )

    Icon(
      painter = painterResource(R.drawable.ic_arrow),
      contentDescription = null
    )

    DropdownInput(
      modifier = Modifier.weight(1f),
      items = options2.toOptions(),
      initial = options2.filter { it.uid == member2?.uid }.toOptions().singleOrNull(),
      label = stringResource(R.string.secret_santa_select_exclusions_input_label2),
      onSelectionChanged = { index ->
        val m2 = index?.let { options2.getOrNull(index) }
        onChange(member1 to m2)
      },
    )

    IconButtonCustom(
      imageVector = Icons.Rounded.Close,
      contentSize = DpSize(24.dp, 24.dp),
      onClick = onRemove,
    )
  }
}

@Composable
private fun InfoBanner() {
  Surface(
    modifier = Modifier.fillMaxWidth(),
    color = WishlifyTheme.colorScheme.infoContainer,
    shape = WishlifyTheme.shapes.small
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Icon(
          imageVector = Icons.Outlined.Info,
          contentDescription = null,
          tint = WishlifyTheme.colorScheme.onInfoContainer
        )

        Text(
          text = stringResource(R.string.secret_santa_new_event_exclusions_skip_banner_title),
          style = WishlifyTheme.typography.bodyMedium,
          color = WishlifyTheme.colorScheme.onInfoContainer,
          fontWeight = FontWeight.Bold
        )
      }

      Text(
        modifier = Modifier.padding(start = 32.dp),
        text = htmlString(R.string.secret_santa_new_event_exclusions_skip_banner_description),
        style = WishlifyTheme.typography.bodyMedium,
        color = WishlifyTheme.colorScheme.onInfoContainer,
      )
    }
  }
}

private fun List<Pair<User.Basic?, User.Basic?>>.filterNotNull() = mapNotNull { (m1, m2) ->
  if (m1 != null && m2 != null) {
    m1 to m2
  } else {
    null
  }
}

private fun List<User.Basic>.toOptions() = mapIndexed { index, participant ->
  DropdownInput.Option(
    id = index,
    text = participant.username,
  )
}