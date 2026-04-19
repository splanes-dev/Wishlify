package com.splanes.uoc.wishlify.presentation.feature.profile.feature.notifications.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.feature.profile.feature.notifications.model.UserProfileNotificationsForm
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@Composable
fun SecretSantaNotificationSection(
  form: UserProfileNotificationsForm,
  modifier: Modifier = Modifier,
  onChange: (form: UserProfileNotificationsForm) -> Unit,
) {
  Surface(
    modifier = modifier,
    shape = WishlifyTheme.shapes.small,
    color = WishlifyTheme.colorScheme.surfaceContainer
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp),
    ) {
      Text(
        text = stringResource(R.string.secret_santa),
        style = WishlifyTheme.typography.titleMedium,
        color = WishlifyTheme.colorScheme.onSurface
      )

      Spacer(Modifier.height(8.dp))

      NotificationSwitch(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(R.string.profile_notifications_secret_santa_chat),
        checked = form.secretSantaChat,
        isBottomDividerVisible = true,
        onCheckedChange = { onChange(form.copy(secretSantaChat = it)) }
      )

      Spacer(Modifier.height(8.dp))

      NotificationSwitch(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(R.string.profile_notifications_secret_santa_deadline),
        checked = form.secretSantaDeadlineReminders,
        isBottomDividerVisible = false,
        onCheckedChange = { onChange(form.copy(secretSantaDeadlineReminders = it)) }
      )
    }
  }
}