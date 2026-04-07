package com.splanes.uoc.wishlify.presentation.feature.groups.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.domain.feature.groups.model.Group
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@Composable
fun GroupStateLabel(
  state: Group.State,
  modifier: Modifier = Modifier,
  size: GroupStateLabel.Size = GroupStateLabel.Small
) {

  val contentColor = when (state) {
    Group.State.Active -> WishlifyTheme.colorScheme.onSuccessContainer
    Group.State.Inactive -> WishlifyTheme.colorScheme.onWarningContainer
  }

  val containerColor = when (state) {
    Group.State.Active -> WishlifyTheme.colorScheme.successContainer
    Group.State.Inactive -> WishlifyTheme.colorScheme.warningContainer
  }

  Surface(
    modifier = modifier,
    shape = WishlifyTheme.shapes.extraSmall,
    color = containerColor
  ) {
    Text(
      modifier = Modifier.padding(
        when (size) {
          GroupStateLabel.Large -> PaddingValues(horizontal = 10.dp, vertical = 6.dp)
          GroupStateLabel.Small -> PaddingValues(horizontal = 6.dp, vertical = 4.dp)
        }
      ),
      text = state.text(),
      style = when (size) {
        GroupStateLabel.Large -> WishlifyTheme.typography.bodyMedium
        GroupStateLabel.Small -> WishlifyTheme.typography.labelMedium
      },
      color = contentColor
    )
  }
}

@Composable
fun Group.State.text() = when (this) {
  Group.State.Active -> R.string.groups_state_active
  Group.State.Inactive -> R.string.groups_state_inactive
}.let { id -> stringResource(id) }

object GroupStateLabel {

  sealed interface Size
  data object Small : Size
  data object Large : Size
}