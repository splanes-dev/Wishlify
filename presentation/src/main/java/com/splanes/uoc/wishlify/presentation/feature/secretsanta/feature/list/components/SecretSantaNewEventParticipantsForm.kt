package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.components

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.domain.common.model.InviteLink
import com.splanes.uoc.wishlify.domain.feature.groups.model.Group
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonShape
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonText
import com.splanes.uoc.wishlify.presentation.common.components.input.dropdown.DropdownInput
import com.splanes.uoc.wishlify.presentation.common.components.input.text.TextInput
import com.splanes.uoc.wishlify.presentation.common.components.input.text.rememberTextInputState
import com.splanes.uoc.wishlify.presentation.common.utils.copyToClipboard
import com.splanes.uoc.wishlify.presentation.common.utils.htmlString
import com.splanes.uoc.wishlify.presentation.feature.groups.feature.list.components.GroupMemberPicker
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.creation.model.SecretSantaNewEventForm
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SecretSantaNewEventParticipantsForm(
  form: SecretSantaNewEventForm,
  groups: List<Group.Basic>,
  inviteLink: InviteLink,
  modifier: Modifier = Modifier,
  isBannerVisible: Boolean = true,
  onCreateGroup: () -> Unit,
  onSearchUsers: () -> Unit,
  onSkipAndCreate: (form: SecretSantaNewEventForm) -> Unit,
  onNext: (form: SecretSantaNewEventForm) -> Unit,
) {

  val context = LocalContext.current
  val resources = LocalResources.current
  val clipboard = LocalClipboard.current
  val scope = rememberCoroutineScope()

  val participants = remember(form.participants) {
    mutableStateListOf(*form.participants.toTypedArray())
  }
  var groupSelected: Group.Basic? by remember { mutableStateOf(form.group) }

  val linkState = rememberTextInputState(initialValue = inviteLink.asUrl())

  val isButtonEnabled by remember(groupSelected, participants) {
    derivedStateOf {
      groupSelected != null || participants.isNotEmpty()
    }
  }

  val options = buildList {
    groups.mapIndexed { index, group ->
      DropdownInput.Option(
        id = index,
        text = group.name,
      )
    }.let(::addAll)
  }
  val items = options + DropdownInput.Button(
    id = 1,
    text = stringResource(R.string.secret_santa_create_group),
    leadingIcon = rememberVectorPainter(Icons.Rounded.Add)
  )

  Column(
    modifier = modifier,
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {

    DropdownInput(
      modifier = Modifier.fillMaxWidth(),
      items = items,
      initial = groups
        .indexOfFirst { it.id == form.group?.id }
        .takeUnless { it == -1 }
        ?.let { index -> options[index] },
      label = stringResource(R.string.secret_santa_select_group_input),
      leadingIcon = Icons.Outlined.Group,
      onSelectionChanged = { index ->
        groupSelected = index?.let { groups.getOrNull(index) }
      },
      onAdd = { onCreateGroup() }
    )

    GroupMemberPicker(
      modifier = Modifier.fillMaxWidth(),
      users = participants,
      onRemoveUser = { participants.remove(it) },
      onSearchUser = onSearchUsers
    )

    TextInput(
      modifier = Modifier.fillMaxWidth(),
      state = linkState,
      label = stringResource(R.string.wishlists_share_list_link),
      leadingIcon = Icons.Rounded.Link,
      trailingIcon = {
        IconButton(
          onClick = {
            scope.launch {
              clipboard.copyToClipboard(
                label = resources.getString(R.string.wishlists_share_list_link),
                text = linkState.text
              )
              Toast.makeText(
                context,
                R.string.feedback_clipboard_copied,
                Toast.LENGTH_SHORT
              ).show()
            }
          }
        ) {
          Icon(
            imageVector = Icons.Rounded.ContentCopy,
            contentDescription = "Copy",
            tint = WishlifyTheme.colorScheme.onSurface.copy(alpha = .5f),
          )
        }
      },
      readOnly = true,
      singleLine = true
    )

    if (isBannerVisible) {
      SecretSantaParticipantsSkippableBanner(onSkip = { onSkipAndCreate(form) })
    }

    Spacer(Modifier.weight(1f))

    Button(
      modifier = Modifier.fillMaxWidth(),
      shapes = ButtonShape,
      enabled = isButtonEnabled,
      onClick = {
        val updated = form.copy(
          participants = participants,
          group = groupSelected
        )
        onNext(updated)
      }
    ) {
      ButtonText(text = stringResource(R.string.next))
    }
  }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SecretSantaParticipantsSkippableBanner(
  onSkip: () -> Unit,
) {
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
          text = stringResource(R.string.secret_santa_new_event_participants_skip_banner_title),
          style = WishlifyTheme.typography.bodyMedium,
          color = WishlifyTheme.colorScheme.onInfoContainer,
          fontWeight = FontWeight.Bold
        )
      }

      Text(
        modifier = Modifier.padding(start = 32.dp),
        text = htmlString(R.string.secret_santa_new_event_participants_skip_banner_description),
        style = WishlifyTheme.typography.bodyMedium,
        color = WishlifyTheme.colorScheme.onInfoContainer,
      )

      OutlinedButton(
        modifier = Modifier.align(Alignment.End),
        shapes = ButtonShape,
        onClick = onSkip,
        border = BorderStroke(width = 1.dp, color = WishlifyTheme.colorScheme.onInfoContainer),
        colors = ButtonDefaults.outlinedButtonColors(
          contentColor = WishlifyTheme.colorScheme.onInfoContainer
        )
      ) {
        ButtonText(
          text = stringResource(R.string.secret_santa_new_event_participants_skip_banner_btn)
        )
      }
    }
  }
}